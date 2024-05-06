package yoon.docker.friendService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "friend")
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long friendIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user", referencedColumnName = "memberIdx")
    private Members fromUser;

    private long toUser;

    @ColumnDefault("false")
    private boolean isFriend;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Friend(Members fromUser, long toUser){
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

}

