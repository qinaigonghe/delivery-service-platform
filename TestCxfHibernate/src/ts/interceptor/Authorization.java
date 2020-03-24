package ts.interceptor;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.XMLMessage;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import antlr.Token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**

 */
public class Authorization extends AbstractPhaseInterceptor<Message> {

	public Authorization() {
		super(Phase.PRE_INVOKE);// 调用之前
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		HttpServletRequest request = (HttpServletRequest) message.get("HTTP.REQUEST");
		HttpServletResponse response = (HttpServletResponse) message.get("HTTP.RESPONSE");
		String uri = (String) message.get(Message.REQUEST_URI);
		System.out.println(uri);
		if (!uri.matches("^/TestCxfHibernate/REST/Misc/doLogin/\\S*$")// 不是登录
				&& !uri.matches("^/TestCxfHibernate/REST/Misc/saveCustomerInfo") // 不拦截添加客户
				&& !uri.matches("^/TestCxfHibernate/REST/Domain/saveExpressSheet")) { // 不是 保存订单
			Cookie[] cookies = request.getCookies();
			// System.out.println(cookies.length);
			String token = null;
			if (cookies != null) {
				for (Cookie cookie : cookies) {
					System.out.println(cookie.getName() + "     " + cookie.getValue());
					if ("token".equals(cookie.getName())) {
						token = cookie.getValue();

						System.out.println("token=" + token);
					}
				}
			} else {
				// todo 其他路径 没有cookies

			}

			// String token = request.getHeader("token");
			try {
				if (token == null || token.length() == 0) { // 没有token
					PrintWriter out = response.getWriter();

					message.getInterceptorChain().doInterceptStartingAfter(message,
							"org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor");
					response.setHeader("message", "你无权访问");
					out.flush();
				} else {
					try {

						JwtUtil.verify(token);
						// token有效
					} catch (Exception e) {
						// token无效
						PrintWriter out = response.getWriter();
						// out.write(new ErrorMessage(ErrorMessage.CODE.TOKEN_ERROR).toString());
						out.write("你无权访问");
						response.setHeader("message", "你无权访问");
						message.getInterceptorChain().doInterceptStartingAfter(message,
								"org.apache.cxf.jaxrs.interceptor.JAXRSOutInterceptor");

						out.flush();
					}
				}
			} catch (IOException e) {
				// e.printStackTrace();
				response.setHeader("message", "你无权访问");
			}
		} else {
			System.out.println("不拦截");
		}
		// HTTP.RESPONSE
		// org.apache.cxf.request.uri
	}

}
