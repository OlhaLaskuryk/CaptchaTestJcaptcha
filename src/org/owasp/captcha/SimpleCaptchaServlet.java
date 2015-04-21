package org.owasp.captcha;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.captcha.Captcha;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.gimpy.DropShadowGimpyRenderer;
import nl.captcha.noise.CurvedLineNoiseProducer;
import nl.captcha.noise.StraightLineNoiseProducer;
import nl.captcha.servlet.CaptchaServletUtil;

import com.octo.captcha.service.CaptchaServiceException;

public class SimpleCaptchaServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5928022862125242647L;
	String sImgType = null;

	public void init(ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);

		// For this servlet, supported image types are PNG and JPG.
		sImgType = servletConfig.getInitParameter("ImageType");
		sImgType = sImgType == null ? "jpg" : sImgType.trim().toLowerCase();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getQueryString() != null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"GET request should have no query string.");
			return;
		}
		try {
			// // Session ID is used to identify the particular captcha.
			// String captchaId = request.getSession().getId();

			// // Clear any existing flag.
			// request.getSession().removeAttribute("PassedCaptcha");
			List<Color> colors = new ArrayList<Color>();
			colors.add(Color.DARK_GRAY);
			colors.add(Color.GRAY);

			List<Font> fonts = new ArrayList<Font>();
			//fonts.add(new Font("name", Font.BOLD, 43));
			//fonts.add(new Font("name2", Font.ITALIC, 45));
			fonts.add(new Font("name3", Font.ROMAN_BASELINE, 43));

			Captcha captcha = new Captcha.Builder(130, 50)
					.addNoise(new StraightLineNoiseProducer(Color.gray, 2))
					.addNoise(new CurvedLineNoiseProducer(Color.gray, 2))

					.addNoise(new StraightLineNoiseProducer(Color.DARK_GRAY, 2))
					.addText(new IntegerTextProducer(4),
							new ColoredWordRenderer(colors, fonts, 3))
					.addNoise(new StraightLineNoiseProducer(Color.gray, 2))
					.addNoise(new CurvedLineNoiseProducer(Color.gray, 2))
					.gimp(new DropShadowGimpyRenderer())
					.addBackground(
							new GradiatedBackgroundProducer(Color.gray,
									Color.lightGray)).addBorder().build(); // Required.
			// display the image produced
			CaptchaServletUtil.writeImage(response, captcha.getImage());

			// save the captcha value on session
			request.getSession().setAttribute("simpleCaptcha", captcha);

		} catch (CaptchaServiceException cse) {
			System.out.println("CaptchaServiceException - " + cse.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Problem generating captcha image.");
			return;
		}

		// Set appropriate http headers.
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// Get the request params.
		Map paramMap = request.getParameterMap();
		if (paramMap.isEmpty()) {
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
					"Post method not allowed without parameters.");
			return;
		}
		String[] arr1 = (String[]) paramMap.get("hidCaptchaID");
		String[] arr2 = (String[]) paramMap.get("inCaptchaChars");
		if (arr1 == null || arr2 == null) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Expected parameters were not found.");
			return;
		}

		String sessId = request.getSession().getId();
		String incomingCaptchaId = arr1.length > 0 ? arr1[0] : "";
		String inputChars = arr2.length > 0 ? arr2[0] : "";

		// Check validity and consistency of the data.
		if (sessId == null || incomingCaptchaId == null
				|| !sessId.equals(incomingCaptchaId)) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Browser must support session cookies.");
			return;
		}

		// Validate whether input from user is correct.
		System.out.println("Validating - inputChars are: " + inputChars);
		boolean passedCaptchaTest = validateCaptcha(incomingCaptchaId,
				inputChars);

		// Set flag into session.
		request.getSession().setAttribute("PassedCaptcha",
				new Boolean(passedCaptchaTest));

		// Forward request to results page.
		RequestDispatcher rd = getServletContext().getRequestDispatcher(
				"/result.jsp");
		rd.forward(request, response);
	}

	private boolean validateCaptcha(String captchaId, String inputChars) {
		boolean bValidated = false;
		try {
			bValidated = MyCaptchaService.getInstance().validateResponseForID(
					captchaId, inputChars);
		} catch (CaptchaServiceException cse) {
		}
		return bValidated;
	}
}