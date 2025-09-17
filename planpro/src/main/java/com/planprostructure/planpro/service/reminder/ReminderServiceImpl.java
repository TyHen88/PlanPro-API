package com.planprostructure.planpro.service.reminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.planprostructure.planpro.enums.ReminderType;
import com.planprostructure.planpro.enums.Status;
import com.planprostructure.planpro.components.common.Pagination;
import com.planprostructure.planpro.components.common.api.MainResposeData;
import com.planprostructure.planpro.domain.myNote.MyNotesRepository;
import com.planprostructure.planpro.domain.reminder.Reminder;
import com.planprostructure.planpro.domain.reminder.ReminderRepository;
import com.planprostructure.planpro.domain.telegramBot.TelegramBotUserRepository;
import com.planprostructure.planpro.domain.trips.TripsRepository;
import com.planprostructure.planpro.domain.users.UserRepository;
import com.planprostructure.planpro.helper.AuthHelper;
import com.planprostructure.planpro.payload.reminder.ReminderRequest;
import com.planprostructure.planpro.payload.reminder.ReminderResponse;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
@RequiredArgsConstructor
public class ReminderServiceImpl implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final UserRepository userRepository;
    private final TripsRepository tripRepository;
    private final MyNotesRepository noteRepository;
    private final TelegramBotUserRepository telegramUserRepository;

    //LOG 
    private static final Logger logger = LoggerFactory.getLogger(ReminderServiceImpl.class);

   @Override
   public void createReminder(ReminderRequest reminderRequest) throws Throwable {
       logger.info("Create Reminder");
       logger.info("Reminder Request: {}", reminderRequest);

       // Validate user authentication
       Long userId = AuthHelper.getUserId();
       if (userId == null) {
           logger.error("User not authenticated");
           throw new IllegalArgumentException("User not authenticated");
       }

       // Get Telegram user if exists
       var telegramUserOpt = telegramUserRepository.findByUserIdAndChatId(userId);
       Long telegramUserId = null;
       if (telegramUserOpt.isPresent()) {
           telegramUserId = telegramUserOpt.get().getChatId();
       } else {
           logger.warn("No Telegram user found for userId {}", userId);
       }

       // Build Reminder entity
       Reminder reminder = Reminder.builder()
           .userId(userId)
           .tripId(reminderRequest.getTripId())
           .noteId(reminderRequest.getNoteId())
           .telegramUserId(telegramUserId)
           .title(reminderRequest.getTitle())
           .description(reminderRequest.getDescription())
           .dueDate(reminderRequest.getDueDate())
           .dueTime(reminderRequest.getDueTime())
           .recurrenceType(reminderRequest.getRecurrenceType())
           .category(reminderRequest.getCategory())
           .createdAt(LocalDateTime.now().toString())
           .lastModified(LocalDateTime.now().toString())
           .status(Status.NORMAL)
           .isStarred(reminderRequest.isStarred())
           .priority(reminderRequest.getPriority())
           .tags(reminderRequest.getTags())
           .reminderStatus(reminderRequest.getReminderStatus())
           .build();

       reminderRepository.save(reminder);
       logger.info("Reminder created successfully for userId {}", userId);
   }

   @Override
   public void updateReminder(Long id, ReminderRequest reminderRequest) throws Throwable {
    var reminder = reminderRepository.findById(id);
    if (reminder.isPresent()) {
        reminder.get().setTitle(reminderRequest.getTitle());
        reminder.get().setDescription(reminderRequest.getDescription());
        reminder.get().setDueDate(reminderRequest.getDueDate());
        reminder.get().setDueTime(reminderRequest.getDueTime());
        reminder.get().setRecurrenceType(reminderRequest.getRecurrenceType());
        reminder.get().setCategory(reminderRequest.getCategory());
        reminder.get().setCreatedAt(reminderRequest.getCreatedAt());
        reminder.get().setLastModified(reminderRequest.getLastModified());
        reminder.get().setStatus(Status.NORMAL);
        reminder.get().setStarred(reminderRequest.isStarred());
        reminder.get().setPriority(reminderRequest.getPriority());
        reminder.get().setTags(reminderRequest.getTags());
        reminder.get().setReminderStatus(reminderRequest.getReminderStatus());
        reminderRepository.save(reminder.get());
        logger.info("Reminder updated successfully for userId {}", reminder.get().getUserId());
    }else{
        logger.error("Reminder not found for id {}", id);
        throw new IllegalArgumentException("Reminder not found");
    }
   }
   
   @Override
   public void deleteReminder(Long id) throws Throwable {
    var reminder = reminderRepository.findById(id);
    if (reminder.isPresent()) {
        reminder.get().setStatus(Status.DISABLE);
        reminderRepository.save(reminder.get());
        logger.info("Reminder deleted successfully for userId {}", reminder.get().getUserId());
    }else{
        logger.error("Reminder not found for id {}", id);
        throw new IllegalArgumentException("Reminder not found");
    }
   }
   
   @Override
   public Object getListReminder(Pageable pageRequest) throws Throwable {
       var reminders = reminderRepository.findAllReminder(AuthHelper.getUserId(), pageRequest);

       List<ReminderResponse> response = reminders.getContent().stream()
               .map(reminder -> ReminderResponse.builder()
                       .id(reminder.getReminderId())
                       .title(reminder.getTitle())
                       .description(reminder.getDescription())
                       .dueDate(reminder.getDueDate())
                       .dueTime(reminder.getDueTime())
                       .category(reminder.getCategory())
                       .priority(reminder.getPriority())
                       .tags(reminder.getTags())
                       .createdAt(reminder.getCreatedAt())
                       .lastModified(reminder.getLastModified())
                       .status(Status.fromValue(reminder.getStatus()) != null
                               ? Status.fromValue(reminder.getStatus()).getLabel()
                               : Status.NORMAL.getLabel())
                       .isRecurring(reminder.getIsRecurring())
                       .recurrenceType(reminder.getRecurrenceType())
                       .isStarred(reminder.getStarred() != null ? reminder.getStarred() : false)
                       .userId(reminder.getUserId())
                       .tripId(reminder.getTripId())
                       .noteId(reminder.getNoteId())
                       .telegramUserId(reminder.getTelegramUserId())
                       .reminderStatus(reminder.getReminderStatus())
                       .build())
               .collect(Collectors.toList());

       return MainResposeData.builder()
               .data(response)
               .pagination(new Pagination(reminders))
               .build();
   }
   
   @Override
   public void markAsDone(Long id, boolean isDone) throws Throwable {
    var reminder = reminderRepository.findById(id);
    if (reminder.isPresent()) {
        reminder.get().setReminderStatus(isDone ? ReminderType.COMPLETED.getValue() : ReminderType.ACTIVE.getValue());
        reminder.get().setStatus(Status.NORMAL);
        reminderRepository.save(reminder.get());
        logger.info("Reminder marked as done for userId {}", reminder.get().getUserId());
    }else{
        logger.error("Reminder not found for id {}", id);
        throw new IllegalArgumentException("Reminder not found");
    }
   }

   @Override
   public void markAsIsStarred(Long id, boolean isStarred) throws Throwable {
    var reminder = reminderRepository.findById(id);
    if (reminder.isPresent()) {
        reminder.get().setStarred(isStarred);
        reminder.get().setStatus(Status.NORMAL);
        reminderRepository.save(reminder.get());
    }
   }
}
