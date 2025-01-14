package ProjectBlogOJT.jwt;

import ProjectBlogOJT.security.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //Kiem tra xem header Authorization co chua thong tin jwt khong
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            //Lay jwt tu request
            String jwt = getJwtFromRequest(request);
            System.out.println(jwt);
            Boolean test = jwtTokenProvider.validateToken(jwt);
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                //Lay userName tu chuoi jwt
                String userNameOrEamil = jwtTokenProvider.getUserNameFromJwt(jwt);
                UserDetails userDetails;
                if(userNameOrEamil.contains("@")){
                    //Lay thong tin nguoi dung tu email
                    userDetails = customUserDetailsService.loadUserByEmail(userNameOrEamil);
                }else {
                    //Lay thong tin nguoi dung tu userName
                    userDetails = customUserDetailsService.loadUserByUsername(userNameOrEamil);
                }
                if (userDetails != null) {
                    //Neu nguoi dung hop le set thong tin cho security context
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            log.error("fail on set user authentication", ex);
        }
        filterChain.doFilter(request, response);
    }
}
