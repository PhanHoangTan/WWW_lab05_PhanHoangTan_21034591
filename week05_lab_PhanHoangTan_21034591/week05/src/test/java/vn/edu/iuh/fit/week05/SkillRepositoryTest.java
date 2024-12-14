package vn.edu.iuh.fit.week05;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import vn.edu.iuh.fit.week05.backend.repositories.SkillRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SkillRepositoryTest {

    @Autowired
    private SkillRepository skillRepository;

    @Test
    public void testFindSkillNameById() {
        // Arrange
        Long skillId = 1L; // Replace with a valid skill ID from your database

        // Act
        String skillName = skillRepository.findSkillNameById(skillId);

        // Assert
        assertNotNull(skillName); // Check that the skill name is not null
        assertEquals("Expected Skill Name", skillName); // Replace with the expected skill name
    }

    @Test
    public void testFindAllSkillNames() {
        // Act
        List<String> skillNames = skillRepository.findAll()
                .stream()
                .map(skill -> skill.getSkillName())
                .toList();

        // Print the skill names
        System.out.println("Skill Names: " + skillNames);
    }
}