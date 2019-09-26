package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;

@SpringBootApplication
public class SalvoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);

	}
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	@Bean
	public CommandLineRunner initData(PlayerRepository playerrepository, GameRepository gamerepository, GamePlayerRepository gameplayerrepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {
			// save a couple of customers
			//here we use the passwordEncoder function
			Player jack = new Player("j.bauer@ctu.gov", passwordEncoder().encode("24"));
			Player chloe = new Player("c.obrian@ctu.gov", passwordEncoder().encode("42"));
			Player kim = new Player("kim_bauer@gmail.com", passwordEncoder().encode("kb"));
			Player tony = new Player("t.almeida@ctu.gov", passwordEncoder().encode("mole"));

			playerrepository.save(jack);
			playerrepository.save(chloe);
			playerrepository.save(kim);
			playerrepository.save(tony);


			Game game1  = new Game();

			//After creating the games, but before saving them, set the dates of the 2 and 3 games to one hour and two hours later, respectively.
			Game game2  = new Game();
			Date date2 = Date.from(game1.getCreationDate().toInstant().plusSeconds(3600));
			game2.setCreationDate(date2);

			Game game3  = new Game();
			Date date3 = Date.from(game1.getCreationDate().toInstant().plusSeconds(7200));
			game3.setCreationDate(date3);

			Game game4  = new Game();
			Date date4 = Date.from(game1.getCreationDate().toInstant().plusSeconds(10800));
			game4.setCreationDate(date4);

			Game game5  = new Game();
			Date date5 = Date.from(game1.getCreationDate().toInstant().plusSeconds(14400));
			game5.setCreationDate(date5);

			Game game6  = new Game();
			Date date6 = Date.from(game1.getCreationDate().toInstant().plusSeconds(18000));
			game6.setCreationDate(date6);

			Game game7  = new Game();
			Date date7 = Date.from(game1.getCreationDate().toInstant().plusSeconds(21600));
			game7.setCreationDate(date7);

			Game game8  = new Game();
			Date date8 = Date.from(game1.getCreationDate().toInstant().plusSeconds(25200));
			game8.setCreationDate(date8);

			gamerepository.save(game1);
			gamerepository.save(game2);
			gamerepository.save(game3);
			gamerepository.save(game4);
			gamerepository.save(game5);
			gamerepository.save(game6);
			gamerepository.save(game7);
			gamerepository.save(game8);


			GamePlayer gp1 = new GamePlayer(jack,game1);
			GamePlayer gp2 = new GamePlayer(chloe, game1);

			GamePlayer gp3 = new GamePlayer(jack, game2);
			GamePlayer gp4 = new GamePlayer(chloe, game2);

			GamePlayer gp5 = new GamePlayer(chloe, game3);
			GamePlayer gp6 = new GamePlayer(tony, game3);

			GamePlayer gp7 = new GamePlayer(chloe, game4);
			GamePlayer gp8 = new GamePlayer(jack, game4);

			GamePlayer gp9 = new GamePlayer(tony, game5);
			GamePlayer gp10 = new GamePlayer(jack, game5);

			GamePlayer gp11 = new GamePlayer(kim, game6);

			GamePlayer gp12 = new GamePlayer(tony, game7);

			GamePlayer gp13 = new GamePlayer(kim, game8);
			GamePlayer gp14 = new GamePlayer(tony, game8);

			gameplayerrepository.save(gp1);
			gameplayerrepository.save(gp2);
			gameplayerrepository.save(gp3);
			gameplayerrepository.save(gp4);
			gameplayerrepository.save(gp5);
			gameplayerrepository.save(gp6);
			gameplayerrepository.save(gp7);
			gameplayerrepository.save(gp8);
			gameplayerrepository.save(gp9);
			gameplayerrepository.save(gp10);
			gameplayerrepository.save(gp11);
			gameplayerrepository.save(gp12);
			gameplayerrepository.save(gp13);
			gameplayerrepository.save(gp14);


			Ship ship1=new Ship("Destroyer",Arrays.asList("H2", "H3", "H4"),gp1);
			Ship ship2=new Ship("Submarine",Arrays.asList("E1", "F1", "G1"),gp1);
			Ship ship3=new Ship("Patrol Boat", Arrays.asList("B4", "B5"),gp1);
			Ship ship4=new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"),gp2);
			Ship ship5=new Ship("Patrol Boat", Arrays.asList( "F1", "F2"),gp2);
			Ship ship6=new Ship("Destroyer",Arrays.asList("B5", "C5", "D5"),gp3);
			Ship ship7=new Ship("Patrol Boat",Arrays.asList("C6","C7"),gp3);
			Ship ship8=new Ship("Submarine",Arrays.asList("A2", "A3", "A4"),gp4);
			Ship ship9=new Ship("Patrol Boat",Arrays.asList("G6","H6"),gp4);
			Ship ship10=new Ship("Destroyer",Arrays.asList("B5","C5","D5"),gp5);
			Ship ship11=new Ship("Patrol Boat",Arrays.asList("C6","C7"),gp5);
			Ship ship12=new Ship("Submarine",Arrays.asList("A2","A3","A4"),gp6);
			Ship ship13=new Ship("Patrol Boat", Arrays.asList("G6", "H6"),gp6);
			Ship ship14=new Ship("Destroyer", Arrays.asList("B5", "C5", "D5"),gp7);
			Ship ship15=new Ship("Patrol Boat",Arrays.asList("C6", "C7"),gp7);
			Ship ship16=new Ship("Submarine",Arrays.asList("A2", "A3", "A4"),gp8);
            Ship ship17=new Ship("Patrol Boat",Arrays.asList("G6", "H6"),gp8);
            Ship ship18=new Ship("Destroyer",Arrays.asList("B5","C5", "D5"),gp9);
            Ship ship19=new Ship("Patrol Boat",Arrays.asList("C6", "C7"),gp9);
            Ship ship20=new Ship("Submarine",Arrays.asList("A2", "A3", "A4"),gp10);
            Ship ship21=new Ship("Patrol Boat",Arrays.asList("G6", "H6"),gp10);
            Ship ship22=new Ship("Destroyer",Arrays.asList("B5", "C5", "D5"),gp11);
            Ship ship23=new Ship("Patrol Boat",Arrays.asList("C6", "C7"),gp11);
            Ship ship24=new Ship("Destroyer",Arrays.asList("B5", "C5", "D5"),gp13);
            Ship ship25=new Ship("Patrol Boat",Arrays.asList("C6", "C7"),gp13);
            Ship ship26=new Ship("Submarine",Arrays.asList("A2", "A3", "A4"),gp14);
            Ship ship27=new Ship("Patrol Boat",Arrays.asList("G6", "H6"),gp14);

            shipRepository.save(ship1);
			shipRepository.save(ship2);
			shipRepository.save(ship3);
			shipRepository.save(ship4);
			shipRepository.save(ship5);
			shipRepository.save(ship6);
			shipRepository.save(ship7);
			shipRepository.save(ship8);
			shipRepository.save(ship9);
			shipRepository.save(ship10);
			shipRepository.save(ship11);
			shipRepository.save(ship12);
			shipRepository.save(ship13);
			shipRepository.save(ship14);
			shipRepository.save(ship15);
			shipRepository.save(ship16);
			shipRepository.save(ship17);
			shipRepository.save(ship18);
			shipRepository.save(ship19);
			shipRepository.save(ship20);
			shipRepository.save(ship21);
			shipRepository.save(ship22);
			shipRepository.save(ship23);
			shipRepository.save(ship24);
			shipRepository.save(ship25);
			shipRepository.save(ship26);
			shipRepository.save(ship27);

			Salvo salvo1 = new Salvo(gp1,Arrays.asList("B5","C5","F1"),1);
			Salvo salvo2 = new Salvo(gp2,Arrays.asList("B4","B5","B6"),1);
			Salvo salvo3 = new Salvo(gp1,Arrays.asList("F2", "D5"),2);
			Salvo salvo4 = new Salvo(gp2,Arrays.asList("E1","H3","A2"),2);
			Salvo salvo5 = new Salvo(gp3,Arrays.asList("A2","A4","G6"),1);
			Salvo salvo6 = new Salvo(gp4,Arrays.asList("B5","D5","C7"),1);
			Salvo salvo7 = new Salvo(gp3,Arrays.asList("A3","H6"),2);
			Salvo salvo8 = new Salvo(gp4,Arrays.asList("C5","C6"),2);
			Salvo salvo9 = new Salvo(gp5,Arrays.asList("G6","H6","A4"),1);
			Salvo salvo10 = new Salvo(gp6,Arrays.asList("H1","H2","H3"),1);
			Salvo salvo11 = new Salvo(gp5,Arrays.asList("A2","A3","D8"),2);
			Salvo salvo12 = new Salvo(gp6,Arrays.asList("E1","F2","G3"),2);
			Salvo salvo13 = new Salvo(gp7,Arrays.asList("A3","A4","F7"),1);
			Salvo salvo14 = new Salvo(gp8,Arrays.asList("B5","C6","H1"),1);
			Salvo salvo15 = new Salvo(gp7,Arrays.asList("A2","G6","H6"),2);
			Salvo salvo16 = new Salvo(gp8,Arrays.asList("C5","C7","D5"),2);
			Salvo salvo17 = new Salvo(gp9,Arrays.asList("A1","A2","A3"),1);
			Salvo salvo18 = new Salvo(gp10,Arrays.asList("B5","B6","C7"),1);
			Salvo salvo19 = new Salvo(gp9,Arrays.asList("G6","G7","G8"),2);
			Salvo salvo20 = new Salvo(gp10,Arrays.asList("C6","D6","E6"),2);
			Salvo salvo21 = new Salvo(gp10,Arrays.asList("H1","H8"),3);

			salvoRepository.save(salvo1);
			salvoRepository.save(salvo2);
			salvoRepository.save(salvo3);
			salvoRepository.save(salvo4);
			salvoRepository.save(salvo5);
			salvoRepository.save(salvo6);
			salvoRepository.save(salvo7);
			salvoRepository.save(salvo8);
			salvoRepository.save(salvo9);
			salvoRepository.save(salvo10);
			salvoRepository.save(salvo11);
			salvoRepository.save(salvo12);
			salvoRepository.save(salvo13);
			salvoRepository.save(salvo14);
			salvoRepository.save(salvo15);
			salvoRepository.save(salvo16);
			salvoRepository.save(salvo17);
			salvoRepository.save(salvo18);
			salvoRepository.save(salvo19);
			salvoRepository.save(salvo20);
			salvoRepository.save(salvo21);

			Score score1 = new Score(jack, game1, 1.0);
			Score score2 = new Score(chloe, game1, 0.0);

			Score score3 = new Score(jack, game2, 0.5);
			Score score4 = new Score(chloe, game2, 0.5);

			Score score5 = new Score(chloe,game3,1.0);
			Score score6 = new Score(tony, game3, 0.0);

			Score score7 = new Score(chloe, game4, 0.5);
			Score score8 = new Score(jack,game4, 0.5);

			scoreRepository.save(score1);
			scoreRepository.save(score2);
			scoreRepository.save(score3);
			scoreRepository.save(score4);
			scoreRepository.save(score5);
			scoreRepository.save(score6);
			scoreRepository.save(score7);
			scoreRepository.save(score8);


		};
	}
}

@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName-> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("Unknown user: " + inputName);
			}
		});

	}
}

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/api/games").permitAll()
				.antMatchers("/api/scores").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/players").permitAll()
				.antMatchers("/api/game_view/**").hasAuthority("USER");



		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");
		//the endpoints /api/login & /api/logout are generated automatically

		// turn off checking for CSRF ( Cross-Site Forgery Request) tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}


}