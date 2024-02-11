package com.amk;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;

public class App {
	private static final String url = "mongodb://localhost";
	private static Scanner sc=new Scanner(System.in);
	private static int id;
	private static String titulo;
	private static ArrayList<String> autores;
	private static String autor;
	private static String editorial;
	private static double precio;
	public static void main(String[] args) {
		int opt;
		do {
			System.out.print("Programa gestión libros: " +
					"\n\t1. Insert libro" +
					"\n\t2. Insert múltiples libros" +
					"\n\t3. Mostrar libros de una editorial" +
					"\n\t4. Mostrar libros de una editorial ay con precio menor a 20 euros" +
					"\n\t5. Mostrar todos los libros de un autor (único o con otros autores)" +
					"\n\t6. Actualizar los precios de todos los libros de una editorial (incremento en 5€)" +
					"\n\t7. Borrar libro por identificador" +
					"\n\t8. Hacer algún upsert" +
					"\n\t9. Mostrar todos los libros" +
					"\n\t0. Salir" +
					"\nOpción: ");
			opt = sc.nextInt();
			sc.nextLine();
			switch (opt){
				case 1:
					InsertOne();
					break;
				case 2:
					InsertMultiple();
					break;
				case 3:
					MuestraEditorial();
					break;
				case 4:
					MuestraEditorial2();
					break;
				case 5:
					MuestraAutor();
					break;
				case 6:
					ActEditorial();
					break;
				case 7:
					BorraLibro();
					break;
				case 8:
					Upsert();
					break;
				case 9:
					MuestraTodo();
					break;
				case 0:
					System.out.println("Hasta luego");
					break;
				default:
					System.out.println("Opción incorrecta");
					break;
			}
		} while (opt != 0);
	}
	public static void InsertOne(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("Id libro: ");
			id=sc.nextInt();
			sc.nextLine();
			Document libro=new Document("_id",id);
			System.out.print("Título libro: ");
			titulo = sc.nextLine();
			System.out.print("Autor/es libro: ");
			autor=sc.nextLine();
			autores=new ArrayList<>(Arrays.asList(autor.split("\\s*,\\s*")));
			System.out.print("Editorial libro: ");
			editorial=sc.nextLine();
			System.out.print("Precio libro: ");
			precio=sc.nextDouble();
			sc.nextLine();
			libro
					.append("titulo",titulo)
					.append("autor/es",autores)
					.append("editorial",editorial)
					.append("precio",precio);
			mcol.insertOne(libro);
		}
	}
	public static void InsertMultiple(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿Cuántos libros quieres insertar? ");
			int cant=sc.nextInt();
			sc.nextLine();
			ArrayList<Document> libros=new ArrayList<>();
			for(int i=0;i<cant;i++){
				System.out.print("Id libro: ");
				id=sc.nextInt();
				sc.nextLine();
				Document libro=new Document("_id",id);
				System.out.print("Título libro: ");
				titulo = sc.nextLine();
				System.out.print("Autor/es libro: ");
				autor=sc.nextLine();
				autores=new ArrayList<>(Arrays.asList(autor.split("\\s*,\\s*")));
				System.out.print("Editorial libro: ");
				editorial=sc.nextLine();
				System.out.print("Precio libro: ");
				precio=sc.nextDouble();
				sc.nextLine();
				libro
						.append("titulo",titulo)
						.append("autor/es",autores)
						.append("editorial",editorial)
						.append("precio",precio);
				libros.add(libro);
			}
			mcol.insertMany(libros,new InsertManyOptions());
		}
	}
	public static void MuestraEditorial(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿De qué editorial quieres mostrar los libros? ");
			String edi=sc.nextLine();
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find(eq("editorial",edi)).forEach(printConsumer);
		}
	}
	public static void MuestraEditorial2(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			Consumer<Document> printConsumer=document -> System.out.print(document.toJson());
			System.out.print("¿De qué editorial quieres mostrar los libros? ");
			String edi=sc.nextLine();
			mcol.find(and(eq("editorial",edi),lt("precio",20))).forEach(printConsumer);
		}
	}
	public static void MuestraAutor(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿De qué autor quieres mostrar los libros? ");
			String aut=sc.nextLine();
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find(eq("autor/es",aut)).forEach(printConsumer);
		}
	}
	public static void ActEditorial(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿Cuál es la editorial a actualizar?(subir precio en 5) ");
			String ediAct=sc.nextLine();
			Document filtrado=new Document();
			filtrado.append("editorial",ediAct);
			Bson actualizar=Updates.inc("precio",5.00);
			mcol.updateMany(filtrado,actualizar);
			System.out.println("Precios después update:");
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find(eq("editorial",ediAct)).forEach(printConsumer);
		}
	}
	public static void BorraLibro(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿Cuál es el id del libro a borrar? ");
			int idBorra=sc.nextInt();
			sc.nextLine();
			Document filtrado=new Document();
			filtrado.append("_id",idBorra);
			mcol.deleteOne(filtrado);
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find().forEach(printConsumer);
		}
	}
	public static void Upsert(){
		//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			System.out.print("¿Cuál es el id del libro a actualizar? ");
			int idAct=sc.nextInt();
			sc.nextLine();
			titulo="Biblia";
			autor="Si,también";
			autores=new ArrayList<>(Arrays.asList(autor.split("\\s*,\\s*")));
			editorial="Claro";
			precio=33.33;
			Bson filtro=eq("_id",idAct);
			Document libro=new Document();
			libro
					.append("titulo",titulo)
					.append("autor/es",autores)
					.append("editorial",editorial)
					.append("precio",precio);
			Bson actualizar= new Document("$set",libro);
			UpdateOptions uo=new UpdateOptions().upsert(true);
			mcol.updateOne(filtro,actualizar,uo);
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find(eq("_id",idAct)).forEach(printConsumer);
		}
	}
	public static void MuestraTodo(){
//Conexión a MongoDB
		try(MongoClient mc= MongoClients.create(url)) {
			//Conexión a DataBase
			MongoDatabase mdb = mc.getDatabase("midb");
			//Conexión a Colección
			MongoCollection<Document> mcol = mdb.getCollection("libros");
			Consumer<Document> printConsumer=document -> System.out.println(document.toJson());
			mcol.find().forEach(printConsumer);
		}
	}
}