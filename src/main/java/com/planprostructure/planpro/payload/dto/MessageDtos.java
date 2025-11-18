package com.planprostructure.planpro.payload.dto;

import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import com.planprostructure.planpro.utils.ClientMsgIdGenerator;

public class MessageDtos {
  @Schema(description = "Request to send a message")
  public record SendRequest(
      @Schema(description = "Client message ID (auto-generated if not provided)", 
              example = "msg_1705123456789_Ax7Kp2Mq")
      String clientMsgId, 
      
      @Schema(description = "Message text content", 
              example = "Hello everyone!")
      String text, 
      
      @Schema(description = "List of file attachments")
      List<AttachmentRef> attachments, 
      
      @Schema(description = "ID of message being replied to", 
              example = "msg_1705123456788_Bx8Lq3Nr")
      String replyTo, 
      
      @Schema(description = "List of mentioned user IDs", 
              example = "[\"user_123\", \"user_456\"]")
      List<String> mentions) {
      
      public SendRequest {
          // Auto-generate clientMsgId 
          if (clientMsgId == null || clientMsgId.isBlank()) {
              clientMsgId = ClientMsgIdGenerator.generateClientMsgId();
          }
      }
  }
  
  @Schema(description = "File attachment reference")
  public record AttachmentRef(
      @Schema(description = "File type", example = "image")
      @NotBlank String type, 
      
      @Schema(description = "File storage key", example = "uploads/images/photo.jpg")
      @NotBlank String objectKey, 
      
      @Schema(description = "Image/video width in pixels", example = "1920")
      Integer width, 
      
      @Schema(description = "Image/video height in pixels", example = "1080")
      Integer height) {}
} 