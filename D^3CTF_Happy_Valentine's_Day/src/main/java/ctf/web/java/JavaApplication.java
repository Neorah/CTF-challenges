package ctf.web.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@SpringBootApplication
@Controller
public class JavaApplication {

	@Autowired
	private ThymeleafViewResolver thymeleafViewResolver;

	public static void main(String[] args) {
		SpringApplication.run(JavaApplication.class, args);
	}

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@PostMapping("/love")
	@ResponseBody
	public String love(@RequestParam(name = "name", defaultValue = "") String name, @RequestHeader(name = "D3H", defaultValue = "") String D3H, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (filter(name)) {
			return "Your girl's name is not lovely";
		}
		try {
			model.addAttribute("name", name);
			WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
			String html = thymeleafViewResolver.getTemplateEngine().process("love", ctx);
			ClassPathResource classPathResource = new ClassPathResource("templates/love.html");
			String path = classPathResource.getFile().getAbsolutePath().replace("love.html", getFilename(!D3H.equals("") ? D3H : request.getServerName()));
			File file = new File(path);
			file.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(html);
			out.close();
			return "Success";
		} catch (IOException e) {
			response.setStatus(500);
			return "500 Internal Server Error";
		}
	}

	@GetMapping("/love")
	public String showLove() {
		return "download";
	}

	@GetMapping("/1nt3na1_pr3v13w")
	public String preview(@RequestHeader(name = "D3H", defaultValue = "") String D3H, HttpServletRequest request) {
		String filename = getFilename(!D3H.equals("") ? D3H : request.getServerName());
		ClassPathResource classPathResource = new ClassPathResource("templates/" + filename);
		if (!classPathResource.exists()) {
			return "error/4xx";
		}
		return filename.replace(".html", "");
	}

	@GetMapping("/download")
	public Object download(@RequestHeader(name = "D3H", defaultValue = "") String D3H, HttpServletRequest request, HttpServletResponse response) {
		try {
			ClassPathResource classPathResource = new ClassPathResource("templates/" + getFilename(!D3H.equals("") ? D3H : request.getServerName()));
			File file = classPathResource.getFile();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
			headers.add("Content-Disposition", "attachment; filename=love.html");
			headers.add("Pragma", "no-cache");
			headers.add("Expires", "0");
			return ResponseEntity
					.ok()
					.headers(headers)
					.contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(new InputStreamResource(new FileInputStream(file)));
		} catch (IOException e) {
			response.setStatus(404);
			return "error/4xx";
		}
	}

	private String getFilename(String url) {
		String[] splitUrl = url.split("\\.");
		String md5 = DigestUtils.md5DigestAsHex((splitUrl[0] + "L0V3").getBytes(StandardCharsets.UTF_8));
		return String.format("love-%s.html", md5);
	}

	private boolean filter(String name) {
		String blacklist = ".*(java\\.lang|Process|Runtime|exec|org\\.springframework|org\\.thymeleaf|" +
				"javax\\.|eval|concat|write|read|forName|param|java\\.io|getMethod|String|T\\(|new|'|\").*";
		return Pattern.matches(blacklist, name);
	}

}
