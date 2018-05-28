package win.hellobro.web.service;

import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Variable;
import team.balam.exof.util.StreamUtil;
import win.hellobro.web.OAuthSite;
import win.hellobro.web.SessionRepository;
import win.hellobro.web.UserInfo;
import win.hellobro.web.service.external.DuplicateException;
import win.hellobro.web.service.external.RemoteUserException;
import win.hellobro.web.service.external.UserClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@ServiceDirectory
public class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Variable
	private String profileImageDir;

	@Variable
	private String profileBaseUrl;

	@ServiceDirectory("/external/user-service")
	private UserClient userClient;

	public void isDuplicateEmailNickname(String email, String nickname) throws IllegalArgumentException, DuplicateException {
		if (StringUtil.isNullOrEmpty(email) || !email.contains("@") || !email.contains(".") ||
				StringUtil.isNullOrEmpty(nickname)) {
			throw new IllegalArgumentException();
		}

		this.userClient.checkEmailAndNickname(email, nickname);
	}

	public void saveUser(UserInfo userInfo) throws RemoteUserException {
		String siteImage = userInfo.getImage();
		userInfo.setImage(this.saveProfileImageFromSessionInfo());

		this.userClient.save(userInfo);

		if (userInfo.getImage().isEmpty()) {
			userInfo.setImage(siteImage);
		}
	}

	public String saveProfileImageFromSessionInfo() {
		String imageFilePath = SessionRepository.getProfileImageFile();
		if (imageFilePath != null) {
			File tempFile = new File(imageFilePath);
			if (tempFile.exists()) {
				String fileName = UUID.randomUUID().toString();
				File target = new File(this.profileImageDir +  fileName);

				try (FileInputStream source = new FileInputStream(tempFile);
				     FileOutputStream profile = new FileOutputStream(target)) {
					StreamUtil.write(source, profile);

					return this.profileBaseUrl + fileName;
				} catch (IOException e) {
					LOG.error("File to save profile image file.", e);
					return "";
				} finally {
					if (!tempFile.delete()) {
						LOG.error("File to delete temp file. {}", tempFile);
						tempFile.deleteOnExit();
					}
				}
			}
		}

		return "";
	}

	public boolean updateUserImage(String userId, String imageUrl) {
		try {
			this.userClient.updateInfo(userId, null, null, imageUrl);

			deleteImageFile(SessionRepository.getUserInfo().getImage());
			return true;
		} catch (Exception e) {
			LOG.error("Fail to update user image.", e);
			deleteImageFile(imageUrl);
			return false;
		}
	}

	private void deleteImageFile(String imageUrl) {
		String[] pathArray = imageUrl.split("/");
		String fileName = pathArray[pathArray.length - 1];
		File imageFile = new File(this.profileImageDir, fileName);

		if (imageFile.exists() && !imageFile.delete()) {
			LOG.error("file to delete old image file. {}", imageFile);
			imageFile.deleteOnExit();
		}
	}

	public boolean updateEmailNickname(String userId, String newEmail, String newNickname) {
		try {
			this.userClient.updateInfo(userId, newEmail, newNickname, null);
			return true;
		} catch (Exception e) {
			LOG.error("Fail to update user email & nickname.", e);
			return false;
		}
	}


}
