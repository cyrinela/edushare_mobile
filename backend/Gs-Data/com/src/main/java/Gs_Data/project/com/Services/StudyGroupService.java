package Gs_Data.project.com.Services;

import Gs_Data.project.com.Entities.GroupConnection;
import Gs_Data.project.com.Entities.StudyGroup;
import Gs_Data.project.com.Repositories.GroupConnectionRepository;
import Gs_Data.project.com.Repositories.StudyGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class StudyGroupService {
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    @Autowired
    private GroupConnectionRepository groupConnectionRepository;

    // Créer un groupe
    public StudyGroup createGroup(StudyGroup group) {
        group.setCode(GenerateCode());
        StudyGroup Res = studyGroupRepository.save(group);
        // add to user to its created group
        groupConnectionRepository.save(new GroupConnection(1L,Res.getId()));
        return Res;
    }

    // Récupérer tous les autre groupes
    public List<StudyGroup> getUnjoinedGroups() {
        List<GroupConnection> Connections = groupConnectionRepository.findByUserIdNot(1L); // replace userId later
        List result = new ArrayList<StudyGroup>();
        for (GroupConnection Connection : Connections) {
            result.add(studyGroupRepository.findById(Connection.getGroupId()));
        }
        return result;
    }

    // Récupérer tous les groupes joinée
    public List<StudyGroup> getJoinedGroups() {
        List<GroupConnection> Connections = groupConnectionRepository.findByUserId(1L); // replace userId later
        List result = new ArrayList<StudyGroup>();
        for (GroupConnection Connection : Connections) {
            result.add(studyGroupRepository.findById(Connection.getGroupId()));
        }
        return result;
    }

    // Rejoindre un groupe
    public boolean joinGroup(Long groupId,String code,Long userId) {
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
        if (group.getCode().matches(code)) {
            groupConnectionRepository.save(new GroupConnection(userId,groupId)); // enregistrer la jointure entre l'utilisateur et le group
            return true;
        }
        return false;
    }

    private String GenerateCode() {
        SecureRandom generator = new SecureRandom();
        Long code = generator.nextLong();
        return Long.toHexString(code).substring(0,10);
    }
}