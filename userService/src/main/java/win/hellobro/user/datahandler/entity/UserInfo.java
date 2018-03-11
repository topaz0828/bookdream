package win.hellobro.user.datahandler.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="USER_INFO")
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserInfo() {}

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="ID")
    private String ID;

    @Column(name="EMAIL")
    private String EMail;

    @Column(name="NICKNAME")
    private String NickName;

    @Column(name="OAUTH_SITE")
    private String OAuthSite;

    @Column(name="UPDATE_DATE")
    private Date UpdateDate;

    @Override
    public String toString() {
        return "user [ID:" + ID + "EMAIL:" + EMail + "NICKNAME:" +
                NickName + "OAUTH_SITE:" + OAuthSite + "UPDATE_DATE" + UpdateDate + "]";
    }



}
