package bgu.spl.mics.application;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.*;


/** get the information from the input json file through Input {@link Input}.
 * create the bus{@link MessageBusImpl}, MicroServices{@link MicroService},
 * Dairy{@link Diary}, Ewoks{@link Ewoks} and Flags{@link Flags}.
 * then start the microServices as Thread.
 * after the thread finish create an output json file, with the information from the dairy
 */
public class Main {
	public static void main(String[] args)  {
		try {
			Input input = makeInput(args[0]);

			MessageBusImpl bus = MessageBusImpl.getInstance();

			LeiaMicroservice leia1 = new LeiaMicroservice( input.getAttacks(), input.getR2D2(), input.getLando());
			HanSoloMicroservice han1 = new HanSoloMicroservice();
			C3POMicroservice c3po1 = new C3POMicroservice();
			R2D2Microservice R2D21 = new R2D2Microservice(input.getR2D2());
			LandoMicroservice lando1 = new LandoMicroservice(input.getLando());

			Thread leia = new Thread(leia1);
			Thread han = new Thread(han1);
			Thread c3po = new Thread(c3po1);
			Thread R2D2 = new Thread(R2D21);
			Thread lando = new Thread(lando1);

			Ewoks ewoks = Ewoks.getInstance(input.getEwoks());
			ewoks.setEwoksList(input.getEwoks());

			Diary diary = Diary.getInstance();
			diary.resetDairy();

			Flags flags = Flags.getInstance();
			flags.resetAll();

			han.start();
			c3po.start();
			lando.start();
			R2D2.start();
			leia.start();

			try {
				lando.join();
				leia.join();
				han.join();
				c3po.join();
				R2D2.join();
			}catch (Throwable ignored){}


			Gson output = new Gson();
			Writer writer = Files.newBufferedWriter(Paths.get(args[1]));
			output.toJson(diary, writer);
			writer.close();

			} catch (Exception ex) {
				ex.printStackTrace();
		}
	}

	/**
	 * decode the information from the input json file to an Input {@link Input} class.
	 * @param filePath the path of the input json file.
	 * @return Input {@link Input} with the decoded information from the input.
	 */
	public static Input makeInput(String filePath) {
		try  {
			Gson gson = new Gson();
			Reader reader = Files.newBufferedReader(Paths.get(filePath));
			return gson.fromJson(reader, Input.class);
		}catch  (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
