package com.lifepulse.util;

import com.lifepulse.dto.JwtValidationResult;
import com.lifepulse.enums.JwtErrorType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT令牌工具类
 * 用于用户登录认证、接口鉴权
 */
@Component
public class JwtUtil {

    // 从配置文件读取密钥和过期时间，和你截图里的@Value写法兼容
    @Value("${jwt.secret:lifePulseSecretKey666888999Life}")
    private String secret;

    @Value("${jwt.expire-time:7200000}")
    private Long expireTime;

    /**
     * 生成签名密钥
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @param role 用户角色
     * @return JWT字符串
     */
    public String createToken(Long userId, String role) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role) // 添加角色信息
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(getKey())
                .compact();
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @return JWT字符串
     * @deprecated 请使用 {@link #createToken(Long, String)} 以包含角色信息。
     */
    @Deprecated
    public String createToken(Long userId) {
        return createToken(userId, "user"); // 默认为普通用户
    }

    /**
     * 解析令牌获取用户ID
     * @param token JWT字符串
     * @return 用户ID
     * @deprecated 此方法在token无效时会直接抛出异常，不够健壮。请使用 {@link #validateToken(String)} 代替。
     */
    @Deprecated
    public Long getUserIdByToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 校验令牌是否过期/无效
     * @param token JWT字符串
     * @return true=已过期/无效，false=有效
     * @deprecated 此方法无法区分具体的错误类型（如过期、签名错误等）。请使用 {@link #validateToken(String)} 代替。
     */
    @Deprecated
    public boolean isTokenExpire(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // 校验过期时间
            return claims.getExpiration().before(new Date());
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 明确捕获过期异常
            return true;
        } catch (Exception e) {
            // 其他异常（签名错误、格式错误）也视为无效
            return true;
        }
    }

    /**
     * 统一校验JWT令牌，并返回结构化的校验结果
     *
     * @param token JWT字符串
     * @return JwtValidationResult 包含校验状态、用户ID和错误类型
     */
    public JwtValidationResult validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return JwtValidationResult.fail(JwtErrorType.ILLEGAL_ARGUMENT);
        }
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = Long.parseLong(claims.getSubject());
            String role = claims.get("role", String.class);
            return JwtValidationResult.success(userId, role);

        } catch (ExpiredJwtException e) {
            return JwtValidationResult.fail(JwtErrorType.EXPIRED);
        } catch (SignatureException e) {
            return JwtValidationResult.fail(JwtErrorType.INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            return JwtValidationResult.fail(JwtErrorType.MALFORMED);
        } catch (IllegalArgumentException e) {
            // 例如，token为空或仅包含空白字符
            return JwtValidationResult.fail(JwtErrorType.ILLEGAL_ARGUMENT);
        } catch (Exception e) {
            // 捕获所有其他未知异常
            return JwtValidationResult.fail(JwtErrorType.UNKNOWN_ERROR);
        }
    }
}