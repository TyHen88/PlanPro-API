package com.planprostructure.planpro.config;


import com.planprostructure.planpro.components.common.api.StatusCode;
import com.planprostructure.planpro.exception.BusinessException;
import com.planprostructure.planpro.service.password.PasswordEncryption;
import com.planprostructure.planpro.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserAuthenticationProvider {
    private final AuthenticationManager authenticationManager;

    public UserAuthenticationProvider(@Qualifier("userAuthProvider")AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    //Not having access to the user’s password
    public Authentication authenticate(String username, String password) throws Exception {
        try {
            var rawPwd = PasswordUtils.decrypt(password);
            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, rawPwd));
        }catch (UsernameNotFoundException ex){
            throw new BusinessException(StatusCode.USER_NOT_FOUND, ex.getMessage());
        } catch (BadCredentialsException e) {
//            throw new BadCredentialsException("Incorrect username or password", e);
            throw new BusinessException(StatusCode.BAD_CREDENTIALS, e);
        } catch (DisabledException e){
            throw new BusinessException(StatusCode.USER_DISABLED, e);
        }
    }
}
