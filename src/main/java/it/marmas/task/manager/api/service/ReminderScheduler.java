package it.marmas.task.manager.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.dto.UserDto;

@Service
public class ReminderScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired
    private TaskService taskService; // Service to access tasks

    @Autowired
    private EmailService emailService; // Service to send emails

    /**
     * Scheduled task that runs every day to check for tasks nearing their deadlines
     * and sends reminder emails to the respective users.
     */
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24) // every 24 hours
    public void checkTasks() {
        logger.info("checkTasksInExpiration");

        // Retrieve tasks that are expiring soon
        Optional<List<TaskDto>> expiringTasks = taskService.findBetweenDates();

        if (expiringTasks.isEmpty()) {
            logger.info("there's no expiring task");
            return;
        }

        List<TaskDto> results = expiringTasks.get();

        // Map to group tasks by user
        Map<UserDto, List<TaskDto>> map = new HashMap<>();
        List<TaskDto> list = null;
        Set<UserDto> keys = new HashSet<>();
        UserDto currentUser = null;

        // Iterate over all expiring tasks
        for (TaskDto res : results) {
            currentUser = res.getUser();
            if (currentUser == null || currentUser.getUsername() == null) {
                continue;
            }

            final String username = currentUser.getUsername();

            // Check if the user is already in the map
            boolean userAlreadyPresent = map.keySet().stream()
                                             .anyMatch(x -> x.getUsername().equals(username));

            if (userAlreadyPresent) {
                // Find the existing UserDto in the keys set to reuse
                for (UserDto u : keys) {
                    if (u.getUsername().equalsIgnoreCase(currentUser.getUsername())) {
                        currentUser = u;
                        break;
                    }
                }
                list = map.get(currentUser);
                logger.info("map contains " + currentUser.getUsername());
                logger.info("using same list");
            } else {
                // Create a new list for this user and add to the map
                logger.info("map doesn't contain " + currentUser.getUsername());
                logger.info("instantiating new list and create key: " + currentUser.getUsername());
                keys.add(currentUser);
                list = new ArrayList<>();
                map.put(currentUser, list);
            }

            // Add the task to the user's list
            logger.info("inserting task " + res.getTitle() + "-" + res.getDescription() + 
                        " in list on behalf of " + currentUser.getUsername());
            list.add(res);
        }

        // Log all tasks grouped by user
        map.entrySet().forEach(e -> {
            logger.info(e.getKey() + "");
            e.getValue().forEach(v -> {
                logger.info(v.getDescription() + "-" + v.getTitle());
            });
        });

        // Send reminder emails to each user
        for (Entry<UserDto, List<TaskDto>> entry : map.entrySet()) {
            UserDto u = entry.getKey();
            List<String> taskList = formatTaskList(entry.getValue());

            emailService.sendReminderEmail(u.getEmail(), u.getUsername(), u.getTimezone(), taskList);
            logger.info("email sent to " + u.getUsername());
        }
    }

    /**
     * Converts a list of TaskDto into a list of formatted HTML strings for the email.
     */
    private List<String> formatTaskList(List<TaskDto> taskList) {
        return taskList.stream()
                       .map(x -> x.getTitle() + "<br /> " + x.getDescription())
                       .toList();
    }
}
