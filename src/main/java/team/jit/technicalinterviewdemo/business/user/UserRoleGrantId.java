package team.jit.technicalinterviewdemo.business.user;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserRoleGrantId implements Serializable {

    private Long userAccount;
    private UserRole role;
}
