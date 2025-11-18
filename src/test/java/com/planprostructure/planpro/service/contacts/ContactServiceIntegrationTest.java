// package com.planprostructure.planpro.service.contacts;

// import com.planprostructure.planpro.domain.chat.Contacts;
// import com.planprostructure.planpro.domain.chatRepo.ContactsRepository;
// import com.planprostructure.planpro.domain.users.UserRepository;
// import com.planprostructure.planpro.domain.users.Users;
// import com.planprostructure.planpro.enums.Role;
// import com.planprostructure.planpro.enums.StatusUser;
// import com.planprostructure.planpro.payload.contacts.AddContactRequest;
// import com.planprostructure.planpro.payload.contacts.ContactResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest
// @ActiveProfiles("test")
// @Transactional
// public class ContactServiceIntegrationTest {

//     @Autowired
//     private ContactService contactService;

//     @Autowired
//     private ContactsRepository contactsRepository;

//     @Autowired
//     private UserRepository userRepository;

//     private Users testUser;

//     @BeforeEach
//     void setUp() {
//         // Create a test user
//         testUser = Users.builder()
//                 .username("testuser")
//                 .email("test@example.com")
//                 .phoneNumber("1234567890")
//                 .firstName("Test")
//                 .lastName("User")
//                 .role(Role.USER)
//                 .status(StatusUser.ACTIVE)
//                 .build();
//         testUser = userRepository.save(testUser);

//         // Set up authentication context
//         Authentication auth = new UsernamePasswordAuthenticationToken(testUser.getId().toString(), null);
//         SecurityContextHolder.getContext().setAuthentication(auth);
//     }

//     @Test
//     void testAddContact() throws Exception {
//         // Given
//         AddContactRequest request = AddContactRequest.builder()
//                 .name("John Doe")
//                 .email("john@example.com")
//                 .phone("9876543210")
//                 .photoUrl("http://example.com/photo.jpg")
//                 .build();

//         // When
//         ContactResponse response = contactService.addContact(request);

//         // Then
//         assertNotNull(response);
//         assertEquals("John Doe", response.getName());
//         assertEquals("john@example.com", response.getEmail());
//         assertEquals("9876543210", response.getPhone());
//         assertEquals("http://example.com/photo.jpg", response.getPhotoUrl());
//         assertNotNull(response.getId());
//         assertNotNull(response.getCreatedAt());
//         assertNotNull(response.getUpdatedAt());
//     }

//     @Test
//     void testGetContacts() throws Exception {
//         // Given
//         AddContactRequest request = AddContactRequest.builder()
//                 .name("Jane Doe")
//                 .email("jane@example.com")
//                 .phone("5555555555")
//                 .build();
//         contactService.addContact(request);

//         // When
//         List<ContactResponse> contacts = contactService.getContacts();

//         // Then
//         assertNotNull(contacts);
//         assertFalse(contacts.isEmpty());
//         assertEquals(1, contacts.size());
//         assertEquals("Jane Doe", contacts.get(0).getName());
//     }

//     @Test
//     void testDeleteContact() throws Exception {
//         // Given
//         AddContactRequest request = AddContactRequest.builder()
//                 .name("Delete Me")
//                 .email("delete@example.com")
//                 .phone("1111111111")
//                 .build();
//         ContactResponse contact = contactService.addContact(request);

//         // When
//         contactService.deleteContact(contact.getId());

//         // Then
//         List<ContactResponse> contacts = contactService.getContacts();
//         assertTrue(contacts.isEmpty());
//     }

//     @Test
//     void testCreateContactsForNewUser() throws Exception {
//         // Given
//         Users existingUser = Users.builder()
//                 .username("existinguser")
//                 .email("existing@example.com")
//                 .phoneNumber("1234567890") // Same phone as testUser
//                 .firstName("Existing")
//                 .lastName("User")
//                 .role(Role.USER)
//                 .status(StatusUser.ACTIVE)
//                 .build();
//         userRepository.save(existingUser);

//         // When
//         contactService.createContactsForNewUser(testUser.getId().toString(), "1234567890");

//         // Then
//         List<Contacts> contacts = contactsRepository.findByUserId(testUser.getId().toString());
//         assertFalse(contacts.isEmpty());
//         assertEquals(1, contacts.size());
//         assertEquals("Existing User", contacts.get(0).getName());
//     }
// } 