package team.jit.technicalinterviewdemo.business.user;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleGrantId implements Serializable {

    private Long userAccount;
    private UserRole role;
}
