package com.firstfewlines;

import static spark.Spark.*;

import com.firstfewlines.model.Task;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.servlet.MultipartConfigElement;
import java.util.List;

public class SparkJavaHibernateExample {

    public static void main(String[] argv) {

        SessionFactory sf = new Configuration().configure().buildSessionFactory();

        staticFiles.location("/public");

        post("/api/task", (req, res) -> {

            EntityManager session = sf.createEntityManager();
            try {
                req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement(""));


                Integer id = Integer.parseInt(req.queryParams("id"));
                String name = req.queryParams("name");

                Task task = new Task();
                task.setId(id);
                task.setName(name);

                session.getTransaction().begin();
                session.persist(task);
                session.getTransaction().commit();

                res.redirect("/list");
                return "";
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            } finally {
                if (session.isOpen()) {
                }
            }
        });


        get("/list", (req, res) -> {
            EntityManager session = sf.createEntityManager();
            try {
                List<Task> tasks = session.createQuery("FROM Task").getResultList();

                StringBuilder builder = new StringBuilder();

                builder.append("<style>\n" +
                        "table {\n" +
                        "    border-collapse: collapse;\n" +
                        "    width: 50%;\n" +
                        "}" +
                        "td, th {\n" +
                        "    border: 1px solid #dddddd;\n" +
                        "    text-align: left;\n" +
                        "    padding: 8px;\n" +
                        "}\n" +
                        "</style>");


                builder.append("<table><tr><th>Task Id</th><th>Task Name</th></tr>\n");
                for (Task task : tasks) {
                    builder.append("<tr><td>" + task.getId() + "</td><td>" + task.getName() + "</td></tr>\n");
                }
                builder.append("</table>\n");

                return builder.toString();
            } catch (Exception e) {
                return "Error: " + e.getMessage();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }

        });
    }
}
