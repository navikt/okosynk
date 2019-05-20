package no.nav.okosynk.cli.spring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

public class ADRolleMapper implements UserDetailsContextMapper, Serializable {

    private static final String BATCH_GRUPPE = "0000-GA-OKOSYNK_BATCH";

    @Override
    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String username, Collection<? extends GrantedAuthority> eksterneRoller) {

        List<GrantedAuthority> interneRoller = new ArrayList<>();

        if (harBatchRolle(eksterneRoller)) {
            interneRoller.add(() -> "ROLE_BATCH_USER");
        }


        return new User(username, "", true, true, true, true, interneRoller);
    }

    private boolean harBatchRolle(Collection<? extends  GrantedAuthority> eksterneRoller) {
        return eksterneRoller.stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(BATCH_GRUPPE::equals);
    }
}
