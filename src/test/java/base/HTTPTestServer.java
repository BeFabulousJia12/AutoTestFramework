package base;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class HTTPTestServer {
    public static void HTTPServerToProcessMsg(){

        try {
            Server server = new Server(8080);

            ServletContextHandler context = new ServletContextHandler(
                    ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new Notify()), "/notify");
            context.addServlet(new ServletHolder(new FeedRate()), "/feedRate");
            context.addServlet(new ServletHolder(new Quality()), "/quality");
            System.out.println("Server is starting...");
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        HTTPTestServer.HTTPServerToProcessMsg();
    }
}
