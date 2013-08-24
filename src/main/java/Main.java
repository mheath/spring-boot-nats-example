import nats.client.Message;
import nats.client.Nats;
import nats.client.spring.EnableNatsAnnotations;
import nats.client.spring.NatsBuilder;
import nats.client.spring.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@EnableAutoConfiguration
@EnableNatsAnnotations
@Configuration
public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	@Bean
	public Nats nats(ApplicationEventPublisher applicationEventPublisher, Environment environment) {
		final Nats nats = new NatsBuilder(applicationEventPublisher)
				.addHost(environment.getProperty("nats-server", "nats://localhost:4222"))
				.connect();
		return nats;
	}

	@Controller
	public static class NatsToWebController {
		private List<BlockingQueue<Message>> queues = new CopyOnWriteArrayList<>();

		@RequestMapping("/")
		public void natsDump(HttpServletResponse response) throws Exception {
			response.setContentType("text/plain;charset=utf-8");
			final PrintWriter writer = response.getWriter();
			final BlockingQueue<Message> queue = new LinkedBlockingQueue<>();
			queues.add(queue);
			try {
				for(;;) {
					final Message message = queue.take();
					writer.printf("%s: %s\n", message.getSubject(), message.getBody());
					writer.flush();
				}
			} finally {
				queues.remove(queue);
			}
		}

		@Subscribe(">")
		public void onMessage(Message message) {
			LOGGER.info("Received NATS message: {}", message);
			for (BlockingQueue<Message> queue : queues) {
				queue.offer(message);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(new Object[] {Main.class, NatsToWebController.class}, args);
	}

}
