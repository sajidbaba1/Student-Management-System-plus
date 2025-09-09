package net.javaguides.sms.service.impl;

import net.javaguides.sms.entity.ChatMessage;
import net.javaguides.sms.repository.ChatMessageRepository;
import net.javaguides.sms.service.ChatbotService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatMessageRepository chatMessageRepository;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Value("${gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    public ChatbotServiceImpl(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String processMessage(String sessionId, String username, String message) {
        String response;
        String userText = message == null ? "" : message.trim();
        try {
            if (geminiApiKey != null && !geminiApiKey.isBlank()) {
                response = callGemini(userText);
                if (response == null || response.isBlank()) {
                    response = generateResponse(userText.toLowerCase());
                }
            } else {
                response = generateResponse(userText.toLowerCase());
            }
        } catch (Exception ex) {
            // Fallback gracefully
            response = generateResponse(userText.toLowerCase());
        }
        
        // Determine intent and confidence
        String intent = detectIntent(userText.toLowerCase());
        double confidence = calculateConfidence(userText.toLowerCase(), intent);
        
        // Save the conversation
        ChatMessage chatMessage = saveMessage(sessionId, username, message, response);
        chatMessage.setIntent(intent);
        chatMessage.setConfidence(confidence);
        chatMessageRepository.save(chatMessage);
        
        return response;
    }

    private String callGemini(String message) throws Exception {
        // Build minimal request body per Google Generative Language API v1beta
        String endpoint = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
                geminiModel, geminiApiKey);

        // System prompt to scope responses
        String systemInstruction = "You are the Student Management System Assistant. Answer briefly and helpfully. If a question involves private data or actions not supported, clarify limitations. Avoid code execution and do not reveal secrets.";

        String body = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"parts\": [ { \"text\": " + jsonString(systemInstruction + "\n\nUser: " + message) + " } ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"generationConfig\": { \"temperature\": 0.3, \"maxOutputTokens\": 256 }\n" +
                "}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(12))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            String respBody = response.body();
            try {
                JsonNode root = objectMapper.readTree(respBody);
                // Typical response: candidates[0].content.parts[0].text
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode textNode = candidates.get(0).path("content").path("parts");
                    if (textNode.isArray() && textNode.size() > 0) {
                        String text = textNode.get(0).path("text").asText("");
                        return text != null ? text.trim() : "";
                    }
                }
            } catch (Exception parseEx) {
                // Ignore and fallback below
            }
        }
        return null;
    }

    private String jsonString(String value) {
        if (value == null) return "\"\"";
        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
        return "\"" + escaped + "\"";
    }

    private String generateResponse(String message) {
        // FAQ-based responses with pattern matching
        if (containsAny(message, "hello", "hi", "hey", "greetings")) {
            return "Hello! I'm the SMS Assistant. I can help you with questions about students, courses, grades, fees, library, and more. How can I assist you today?";
        }
        
        if (containsAny(message, "help", "what can you do", "commands")) {
            return "I can help you with:\n• Student information and enrollment\n• Course details and schedules\n• Grade inquiries\n• Fee payments and status\n• Library books and issues\n• Attendance records\n• Assignment submissions\n• General system navigation\n\nJust ask me anything!";
        }
        
        if (containsAny(message, "student", "enrollment", "enroll")) {
            return "For student-related queries:\n• To view all students, go to the Students page\n• To enroll a student in a course, use the enrollment form\n• To check attendance, visit the Attendance section\n• For student grades, check the Grades page\n\nNeed help with a specific student? Please provide more details.";
        }
        
        if (containsAny(message, "grade", "marks", "score", "gpa")) {
            return "Grade Management:\n• Teachers can add grades through the Grades page\n• Students can view their grades in the student portal\n• GPA is automatically calculated\n• Grade reports can be exported as CSV\n\nWould you like help with entering or viewing grades?";
        }
        
        if (containsAny(message, "fee", "payment", "dues", "finance")) {
            return "Fee Management:\n• View all fees in the Fees section\n• Process payments through the payment form\n• Check overdue fees in the overdue section\n• Students/parents can view fee status in their portal\n\nNeed help with a specific fee inquiry?";
        }
        
        if (containsAny(message, "library", "book", "issue", "return")) {
            return "Library System:\n• Browse books in the Library Books section\n• Issue books to students with due dates\n• Track returns and calculate fines\n• View overdue books for follow-up\n\nLooking for a specific book or need help with book management?";
        }
        
        if (containsAny(message, "assignment", "homework", "submit")) {
            return "Assignment Management:\n• Teachers can create assignments with due dates\n• Students can submit assignments online\n• Track submission status and grades\n• View all assignments in the Assignments section\n\nNeed help creating or submitting an assignment?";
        }
        
        if (containsAny(message, "attendance", "present", "absent")) {
            return "Attendance Tracking:\n• Mark attendance through the Mark Attendance page\n• View attendance reports by student or date\n• Track attendance trends in the dashboard\n• Generate attendance summaries\n\nNeed help with attendance management?";
        }
        
        if (containsAny(message, "message", "communication", "announcement")) {
            return "Messaging System:\n• Send personal messages to users\n• Broadcast announcements to all users\n• View inbox, sent messages, and announcements\n• Messages support different types and priorities\n\nWould you like help with sending a message?";
        }
        
        if (containsAny(message, "dashboard", "analytics", "report", "export")) {
            return "Dashboard & Analytics:\n• View system statistics on the dashboard\n• Interactive charts show trends and distributions\n• Export data as CSV files\n• Role-based quick actions for different users\n\nLooking for specific analytics or reports?";
        }
        
        if (containsAny(message, "login", "password", "access", "permission")) {
            return "Access & Security:\n• Use the Login button in the top navigation\n• Different roles have different permissions\n• Contact your administrator for password resets\n• Logout when finished for security\n\nHaving trouble accessing the system?";
        }
        
        if (containsAny(message, "course", "subject", "class", "schedule")) {
            return "Course Management:\n• View all courses in the Courses section\n• Check course schedules in Timetables\n• Enroll students in courses\n• Track course-wise student performance\n\nNeed help with a specific course?";
        }
        
        if (containsAny(message, "teacher", "staff", "faculty")) {
            return "Teacher/Staff Information:\n• View all teachers in the Teachers section\n• Teachers can manage grades and assignments\n• Staff have role-based access to different features\n• Contact information is available in teacher profiles\n\nLooking for a specific teacher or staff member?";
        }
        
        if (containsAny(message, "thank", "thanks", "bye", "goodbye")) {
            return "You're welcome! Feel free to ask if you need any more help with the Student Management System. Have a great day!";
        }
        
        // Default response for unrecognized queries
        return "I'm not sure I understand that question. I can help you with:\n• Students & Enrollment\n• Grades & Assignments\n• Fees & Payments\n• Library Management\n• Attendance Tracking\n• Messages & Announcements\n\nCould you please rephrase your question or ask about one of these topics?";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String detectIntent(String message) {
        if (containsAny(message, "hello", "hi", "hey")) return "greeting";
        if (containsAny(message, "help", "what can you do")) return "help";
        if (containsAny(message, "student", "enrollment")) return "student_inquiry";
        if (containsAny(message, "grade", "marks", "gpa")) return "grade_inquiry";
        if (containsAny(message, "fee", "payment", "dues")) return "fee_inquiry";
        if (containsAny(message, "library", "book")) return "library_inquiry";
        if (containsAny(message, "assignment", "homework")) return "assignment_inquiry";
        if (containsAny(message, "attendance", "present")) return "attendance_inquiry";
        if (containsAny(message, "message", "announcement")) return "message_inquiry";
        if (containsAny(message, "dashboard", "analytics")) return "analytics_inquiry";
        if (containsAny(message, "course", "subject")) return "course_inquiry";
        if (containsAny(message, "teacher", "staff")) return "teacher_inquiry";
        if (containsAny(message, "thank", "bye")) return "farewell";
        return "unknown";
    }

    private double calculateConfidence(String message, String intent) {
        // Simple confidence calculation based on keyword matches
        if ("unknown".equals(intent)) return 0.3;
        if ("greeting".equals(intent) || "farewell".equals(intent)) return 0.95;
        if ("help".equals(intent)) return 0.9;
        return 0.8; // Default confidence for recognized intents
    }

    @Override
    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    @Override
    public List<ChatMessage> getUserChatHistory(String username) {
        return chatMessageRepository.findByUsernameOrderByTimestampDesc(username);
    }

    @Override
    public ChatMessage saveMessage(String sessionId, String username, String userMessage, String botResponse) {
        ChatMessage message = new ChatMessage(sessionId, username, userMessage, botResponse);
        return chatMessageRepository.save(message);
    }

    @Override
    public String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public List<ChatMessage> getRecentMessages(int hours) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(hours);
        return chatMessageRepository.findRecentMessages(startDate);
    }

    @Override
    public List<ChatMessage> getUnresolvedMessages() {
        return chatMessageRepository.findUnresolvedMessages();
    }

    @Override
    public Long getTotalMessagesToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return chatMessageRepository.countMessagesSince(startOfDay);
    }

    @Override
    public List<Object[]> getTopIntents() {
        return chatMessageRepository.findTopIntents();
    }
}
