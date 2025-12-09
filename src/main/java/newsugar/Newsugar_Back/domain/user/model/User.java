package newsugar.Newsugar_Back.domain.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column( name = "name")
    private String name;

    @NotBlank
    @Column( name = "email")
    private String email;

    @NotBlank
    @Column( name = "password")
    private String password;

    @NotBlank
    @Column( name = "nickname")
    private String nickname;

    @Column( name = "phone")
    private String phone;

    @Column( name = "created_at")
    private Instant createdAt;

    @Column( name = "updated_at")
    private Instant updatedAt;
}