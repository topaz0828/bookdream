package win.hellobro.user.datahandler.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "USER_INFO")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserInfo() {
    }

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "EMAIL")
    private String eMail;

    @Column(name = "NICKNAME")
    private String nickName;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "OAUTH_SITE")
    private String OAuthSite;

    @Column(name = "UPDATE_DATE")
    private Date updateDate;

	@Column(name = "OAUTH_ID")
    private String oauthId;

    @Override
    public String toString() {
        return "user [ID:" + id + "EMAIL:" + eMail + "NICKNAME:" + nickName
                + "OAUTH_SITE:" + OAuthSite + "UPDATE_DATE"
                + updateDate + "OAUTH_ID" + oauthId +"IMAGE" + image + "]";
    }


}
