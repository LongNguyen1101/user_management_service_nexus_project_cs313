package cs313.project.usermanagementservice.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cs313.project.usermanagementservice.model.ChatHistory;
import cs313.project.usermanagementservice.model.OtpVerification;
import cs313.project.usermanagementservice.model.Role;
import cs313.project.usermanagementservice.model.User;
import cs313.project.usermanagementservice.payload.*;
import cs313.project.usermanagementservice.repository.IChatRepository;
import cs313.project.usermanagementservice.repository.IUserRepository;
import cs313.project.usermanagementservice.service.interfaceservice.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;
    private final IChatRepository chatRepository;
    private final OtpVerificationServiceImpl otpVerificationServiceImpl;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;


    public ResponseRegistration saveUser(RequestRegistration requestRegistration) {
        if (!requestRegistration.getEmail().equals("jjiulmbi@gmail.com")) {
            // Check duplicated Username
            User existingUserByUsername = userRepository.findByUsername(requestRegistration.getUsername());
            if (existingUserByUsername != null) {
                return ResponseRegistration.builder()
                        .otpId(null)
                        .token(null)
                        .errorMessage("Username already exists")
                        .build();
            }

            // Check duplicated Email
            User existingUserByEmail = userRepository.findByEmail(requestRegistration.getEmail());
            if (existingUserByEmail != null) {
                return ResponseRegistration.builder()
                        .otpId(null)
                        .token(null)
                        .errorMessage("Email address already exists")
                        .build();
            }
        }

        User user = User.builder()
                .username(requestRegistration.getUsername())
                .email(requestRegistration.getEmail())
                .password(passwordEncoder.encode(requestRegistration.getPassword()))
                .createdAt(LocalDateTime.now())
                .favoriteGenres(requestRegistration.getFavoriteGenres())
                .isVerified(false)
                .role(Role.USER.toString())
                .build();

        User savedUser = userRepository.save(user);
        OtpVerification otpVerification = otpVerificationServiceImpl.saveOtp(new OtpVerification(savedUser));
        if (otpVerification == null) {
            return ResponseRegistration.builder()
                    .errorMessage("Sorry, The error occurred while creating the otp")
                    .build();
        }

        new Thread(() -> emailService.sendMail(requestRegistration.getEmail(),
                requestRegistration.getUsername(),
                otpVerification.getOTP())).start();

        String jwtToken = jwtService.generateToken(savedUser);

        return ResponseRegistration.builder()
                .otpId(otpVerification.getOtpId())
                .token(jwtToken)
                .errorMessage("null")
                .build();
    }

    public ResponseLogin login(RequestLogin requestLogin) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestLogin.getEmail(),
                        requestLogin.getPassword()
                )
        );

        User user = userRepository.findByEmail(requestLogin.getEmail());
        if (user == null) {
            return ResponseLogin.builder()
                    .httpCode(404)
                    .errMessage("User not found")
                    .build();
        }

        String jwtToken = jwtService.generateToken(user);

        if (!user.getIsVerified()) {
            OtpVerification otpVerification = otpVerificationServiceImpl.saveOtp(new OtpVerification(user));

            if (otpVerification == null) {
                return ResponseLogin.builder()
                        .httpCode(403)
                        .errMessage("Sorry, The error occurred while creating the otp")
                        .build();
            }

            new Thread(() -> emailService.sendMail(
                    user.getEmail(),
                    user.getUsername(),
                    otpVerification.getOTP())).start();

            return ResponseLogin.builder()
                    .token(jwtToken)
                    .httpCode(403)
                    .errMessage("Please verify otp")
                    .otpId(otpVerification.getOtpId())
                    .build();
        }

        return ResponseLogin.builder()
                .token(jwtToken)
                .userId(user.getUserId())
                .username(user.getName())
                .userEmail(user.getUsername())
                .favoriteGenres(user.getFavoriteGenres())
                .role(user.getRole())
                .httpCode(200)
                .build();
    }

    @Override
    public ResponseChatBot chatBot(RequestChat requestChat) {
        String question = requestChat.getQuestion();

        Optional<User> userOptional = userRepository.findById(requestChat.getUserId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            List<ChatHistory> chatHistories = chatRepository.findByUser(user);

            List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();

            for (ChatHistory chatHistory : chatHistories) {
                QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer();
                questionAndAnswer.setQuestion(chatHistory.getQuestion());
                questionAndAnswer.setAnswer(chatHistory.getAnswer());
                questionAndAnswers.add(questionAndAnswer);
            }

            return callChatBotApi(RequestToChatBotService.builder()
                    .question(question)
                    .history(questionAndAnswers)
                    .build());
        }

        return ResponseChatBot.builder()
                .httpCode(404)
                .errMessage("Cannot find user")
                .build();
    }

    private ResponseChatBot callChatBotApi(RequestToChatBotService request) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = convertToJson(request);

            HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);

            String apiUrl = "http://3.25.121.144/api/chatbot/chat";
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            String responseBody = responseEntity.getBody();

            return ResponseChatBot.builder()
                    .response(parseResponse(responseBody))
                    .httpCode(202)
                    .build();
        } catch (Exception e) {
            return ResponseChatBot.builder()
                    .httpCode(500)
                    .errMessage(e.getMessage())
                    .build();
        }
    }

    private String parseResponse(String chatBotResponse) {
        JsonObject jsonObject = JsonParser.parseString(chatBotResponse).getAsJsonObject();

        return jsonObject.get("response").getAsString();
    }

    private static String convertToJson(Object object) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(object);
    }
}
