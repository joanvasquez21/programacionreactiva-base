package com.springboot.reactor.app;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.reactor.app.models.Comentarios;
import com.springboot.reactor.app.models.Usuario;
import com.springboot.reactor.app.models.UsuarioComentarios;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// ejemploIterable();
		// ejemploFlatMap();
		// ejemploCollectList();s
		// ejemploUsuarioComentariosFlatMap();
		// ejemploToString();
		// ejemploUsuarioComentariosZipWith();
		//contrapresion();

	}
	
	
	
	// Observable con el metodo create
	public void ejemploIntervalDesdeCreate() {
		Flux.create(emitter -> {

			Timer timer = new Timer();

			timer.schedule(new TimerTask() {

				private Integer contador = 0;

				@Override
				public void run() {
					// TODO Auto-generated method stub
					emitter.next(++contador);
					if (contador == 10) {
						timer.cancel();
						emitter.complete();
					}
				}
			}, 1000, 1000);
		}).doOnNext(next -> log.info(next.toString()))
		.doOnComplete( () ->log.info("Hemos terminado"))
		.subscribe();
	}

	public void ejemploDelayElements() throws InterruptedException {

		Flux<Integer> rango = Flux.range(1, 12).delayElements(Duration.ofSeconds(1))
				.doOnNext(i -> log.info(i.toString()));
		rango.subscribe();

		Thread.sleep(13000);
	}



	public void ejemploInterval() {
		Flux<Integer> rango = Flux.range(1, 12);
		Flux<Long> retraso = Flux.interval(Duration.ofSeconds(1));

		rango.zipWith(retraso, (ra, re) -> ra).doOnNext(i -> log.info(i.toString())).blockLast();
	}

	public void ejemploZipWithRange() {

		Flux.just(1, 2, 3, 4).map(i -> (i * 2))
				.zipWith(Flux.just(1, 2, 3, 4),
						(uno, dos) -> String.format("Primer flux: %d, Segundo flux: %d", uno, dos))
				.subscribe(texto -> log.info(texto));
	}

	public void ejemploUsuarioComentariosZipWithForma2() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola");
			comentarios.addComentarios("Hola miercoles");
			comentarios.addComentarios("Hola jueves");

			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioconComentarios = usuarioMono.zipWith(comentariosUsuarioMono).map(tuple -> {
			// item del flujo original
			Usuario u = tuple.getT1();
			Comentarios c = tuple.getT2();
			return new UsuarioComentarios(u, c);
		});

		usuarioconComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public Usuario crearUsuario() {
		return new Usuario("John", "Doe");
	}

	public void ejemploUsuarioComentariosZipWith() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola");
			comentarios.addComentarios("Hola miercoles");
			comentarios.addComentarios("Hola jueves");

			return comentarios;
		});

		Mono<UsuarioComentarios> usuarioconComentarios = usuarioMono.zipWith(comentariosUsuarioMono,
				(usuario, comentariosUsuario) -> new UsuarioComentarios(usuario, comentariosUsuario));
		usuarioconComentarios.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploUsuarioComentariosFlatMap() {
		Mono<Usuario> usuarioMono = Mono.fromCallable(() -> new Usuario("Jhon", "Doe"));

		Mono<Comentarios> comentariosUsuarioMono = Mono.fromCallable(() -> {
			Comentarios comentarios = new Comentarios();
			comentarios.addComentarios("Hola");
			comentarios.addComentarios("Hola miercoles");
			comentarios.addComentarios("Hola jueves");

			return comentarios;

		});

		usuarioMono.flatMap(u -> comentariosUsuarioMono.map(c -> new UsuarioComentarios(u, c)))
				.subscribe(uc -> log.info(uc.toString()));
	}

	public void ejemploCollectList() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();

		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Sam", "Altman"));
		usuariosList.add(new Usuario("Larry", "Ellison"));
		usuariosList.add(new Usuario("Cesar", "Marcos"));
		usuariosList.add(new Usuario("Luis", "Alonso"));
		usuariosList.add(new Usuario("Larry", "Ellison"));

		Flux.fromIterable(usuariosList)
				// collectList convierte a un mono la lista de usuario
				.collectList().subscribe(lista -> {
					lista.forEach(item -> log.info(item.toString()));
				});
	}

	// Convirtiendo un FLUXLIST a un FLUXSTRING
	public void ejemploToString() throws Exception {

		List<Usuario> usuariosList = new ArrayList<>();

		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Diego", "Fulano"));
		usuariosList.add(new Usuario("Diego", "Fulano"));

		Flux.fromIterable(usuariosList)
				.map(usuario -> usuario.getNombre().toUpperCase().concat(usuario.getApellido().toUpperCase()))
				.flatMap(nombre -> {
					if (nombre.contains("bruce".toUpperCase())) {
						return Mono.just(nombre);
					} else {
						return Mono.empty();
					}
				}).map(nombre -> {
					return nombre.toLowerCase();
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploFlatMap() throws Exception {

		List<String> usuariosList = new ArrayList<>();

		usuariosList.add("Diego Fulano");
		usuariosList.add("Juan Alvarado");
		usuariosList.add("Bruce Leee");
		usuariosList.add("Bruce Willy");
		usuariosList.add("Nick Nicole");

		Flux.fromIterable(usuariosList)
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.flatMap(usuario -> {
					if (usuario.getNombre().equalsIgnoreCase("bruce")) {
						return Mono.just(usuario);
					} else {
						return Mono.empty();
					}
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				}).subscribe(u -> log.info(u.toString()));
	}

	public void ejemploIterable() throws Exception {

		List<String> usuariosList = new ArrayList<>();

		usuariosList.add("Diego Fulano");
		usuariosList.add("Juan Alvarado");
		usuariosList.add("Bruce Leee");
		usuariosList.add("Bruce Willy");
		usuariosList.add("Nick Nicole");

		Flux<String> nombres = Flux.fromIterable(
				usuariosList); /* Flux.just("Diego Fulano", "Juan Alvarado", "Bruce Leee", "Bruce Willy"); */

		Flux<Usuario> usuarios = nombres
				.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
				.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce")).doOnNext(usuario -> {
					if (usuario == null) {
						throw new RuntimeException("Nombres no pueden ser vacios");
					}

					System.out.println(usuario.getNombre().concat(" ").concat(usuario.getApellido()));
				}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});

		usuarios.subscribe(nombre -> log.info(nombre.getNombre()), error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("Ha finalizado la ejecucion del observable con exito!");
					}
				});
	}

}
