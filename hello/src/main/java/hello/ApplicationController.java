package hello;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class ApplicationController {

	private static String template = "Hello, %s!";
	private static String succes = "Congratulations, here is the flag! flag{%s}";
	private static String failed = "Wrong Flag! flag{%s}";
	private static String uuid = "3858FDF6-E53A-47AF-86FD-8CB3830B518F";
	private static String crackd = "C4reFu!withEnV";
	private final AtomicLong counter = new AtomicLong();

	@GetMapping("/flag")
	public Application flag(@RequestParam(value = "flag", defaultValue = "Flag") String flag) {
		if (uuid.equals(flag)) {
			return new Application(counter.incrementAndGet(), String.format(succes, crackd));
		}
		else {
			return new Application(counter.incrementAndGet(), String.format(failed, flag));
		}
	}

        @RequestMapping("/")
        public String home() {
                return "Checkout /flag endpoint!\nIt accepts query string flag\n";
        }

}
