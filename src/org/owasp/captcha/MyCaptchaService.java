package org.owasp.captcha;

import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.multitype.GenericManageableCaptchaService;


public class MyCaptchaService {
	// a singleton class
	private static ImageCaptchaService instance= new DefaultManageableImageCaptchaService();
	

	public static ImageCaptchaService getInstance() {
		System.out.println("get instance " + instance);
		return instance;
	}
}