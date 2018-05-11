package com.example.demo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.stream.IntStream;

public class Batch {

	public static void main(String[] args) {

		PrintWriter writer;
		try {
			writer = new PrintWriter("/home/freddylemus/Documents/Lending/TrackingMarketing/report/upload/ICOMMKT_LOG_retorno_Ejemplo_Batch.csv", "UTF-8");
			IntStream.range(1, 8888888).forEach(i ->
			writer.println(
					i + "  Cuenta,Nombre_Campa�a,Nombre_Envio,Nombre_Newsletter,Asunto Comunicaci�n,REGULAR,2004 PARTNERCONNECT DIST,email@email.com,2016-04-20 16:08:11.380,0,0, , ,DESUSCRIPCION, , , , ,Unsubscribe Request,2016-04-20 14:30:00.000"));
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
