/* 
Authors: Daniel Scragg and Johan de Clercq
Info:
    A simple special purpose HTTP server that serves a calculator
*/
//IMPORTS
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{

    // ------ GLOBAL VARIABLES ------
    private static ServerSocket serverSocket;
    private static int port;
    private static boolean running = false;
    private static String ANSWER = "";
    private static String MEMANS = "";
    private static String[] answerCalculator1 = new String[0];
    private static String[] answerCalculator2 = new String[0];
    // ------------------------------

    public static void main(String[] args){
        //Initialise variables
        port = Integer.parseInt(args[0]);

        //Run Server
        setupServer();
        serverLoop();
    }

    private static void serveWebsite(Socket sock){
        // ---------- CONVERT WEBSITE WITH NEW ANSWER TO STRING ----------
        String res =    "HTTP/1.1 200 OK\nContent-Type: text/html\n\n"+
                        "<html style=\"background-color: #363636;\" lang=\"en\">"+"\n"+
                        "<head>"+"\n"+
                        "<title>Calculator</title>"+"\n"+
                        "</head>"+"\n"+
                        "<body>"+"\n"+
                        "<center>"+"\n"+
                        "<textarea readonly style=\"margin-top: 100px;height: 50px; width:160px; font-size: 15px;resize: none;\">"+ANSWER+"</textarea><br/>"+"\n"+
                        "<a href=\"1\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">1</button></a>"+"\n"+
                        "<a href=\"2\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">2</button></a>"+"\n"+
                        "<a href=\"3\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">3</button></a><br/>"+"\n"+
                        "<a href=\"4\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">4</button></a>"+"\n"+
                        "<a href=\"5\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">5</button></a>"+"\n"+
                        "<a href=\"6\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">6</button></a><br/>"+"\n"+
                        "<a href=\"7\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">7</button></a>"+"\n"+
                        "<a href=\"8\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">8</button></a>"+"\n"+
                        "<a href=\"9\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">9</button></a><br/>"+"\n"+
                        "<a href=\"add\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">+</button></a>"+"\n"+
                        "<a href=\"0\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">0</button></a>"+"\n"+
                        "<a href=\"sub\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">-</button></a><br/>"+"\n"+
                        "<a href=\"mult\"><button style=\"margin-top:5px;height: 50px; width:50px; font-size: 20px;\">x</button></a>"+"\n"+
                        "<a href=\"div\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">/</button></a>"+"\n"+
                        "<a href=\"eq\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:50px; font-size: 20px;\">=</button></a><br/>"+"\n"+
                        "<a href=\"/\"><button style=\"margin-top:5px;height: 50px; width:90px; font-size: 20px;\">CLEAR</button></a>"+"\n"+
                        "<a href=\"mem\"><button style=\"margin-left:5px;margin-top:5px;height: 50px; width:70px; font-size: 20px;\">MEM</button></a>"+"\n"+
                        "</center>"+"\n"+
                        "</body>"+"\n"+
                        "</html>";
        // ---------------------------------------------------------------
        // ----------- SEND WEBSITE IN HTTP RESPONSE TO CLIENT -----------
        try{
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

    private static void serverLoop(){
        // ----------- WAIT FOR CONNECTION, READ REQUESTS AND HANDLE THEM -----------
        while(running){
            try{
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // -------- Handle Request --------
                handleRequest(in, socket);
                // --------------------------------
                
            }catch(Exception e){
                running = false;
                e.printStackTrace();
            }
        }
        // --------------------------------------------------------------------------
    }

    private static void setupServer(){
        // ----------- CREATE SERVER SOCKETS AND BIND TO PORT -----------
        try{
            serverSocket = new ServerSocket(port);
            running = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        
        if(running){
            System.out.println("Server running on port: "+port);
        }else{
            System.out.println("ERROR: Server failed to start on port: "+port+"!");
        }
        // --------------------------------------------------------------
    }

    //helper function
    private static boolean isOperant(String input){
        if(input.equals("+") || input.equals("/") || input.equals("-") || input.equals("x")){
            return true;
        }else if(input.equals("add") || input.equals("div") || input.equals("sub") || input.equals("mult")){
            return true;
        }
        return false;
    }
    private static String getOperant(String input){
        if(input.equals("sub")){
            return "-";
        }else if(input.equals("mult")){
            return "x";
        }else if(input.equals("div")){
            return "/";
        }else if(input.equals("add")){
            return "+";
        }
        return null;
    }

    //function for creating the string of calculation operators
    private static void handleInput(String input){
        //exceptions: 01, +*, 
        int lengthOfAnswer = ANSWER.length();
        try{
            if(isOperant(input)){
                //check that operator is not the first input
                if(lengthOfAnswer == 1){
                    ANSWER += getOperant(input);
                }else if(lengthOfAnswer >= 2){
                    //get substrings 
                    String sub1 = ANSWER.substring(lengthOfAnswer-1);
                    String sub2 = ANSWER.substring(lengthOfAnswer-2, lengthOfAnswer-1);

                    if(input.equals("sub")){
                        //System.out.println(sub1);
                        //System.out.println(sub2);
                        if(!isOperant(sub2) && isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }else if(isOperant(sub2) && !isOperant(sub1)){
                            ANSWER += getOperant(input);
                        } else if(!isOperant(sub2) && !isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }
                    }else{
                        if(!isOperant(sub1)){
                            ANSWER += getOperant(input);
                        }
                    }
                }
            }else if(input.equals("mem")){
                ANSWER += MEMANS;
            }else{
                if(lengthOfAnswer >= 2){
                    String sub1 = ANSWER.substring(lengthOfAnswer-1);
                    String sub2 = ANSWER.substring(lengthOfAnswer-2, lengthOfAnswer-1);

                    if(isOperant(sub1) && !input.equals("0")){
                        ANSWER += input;
                    }else if(!isOperant(sub1)){
                        ANSWER += input;
                    }
                }else{
                    if(!input.equals("0")){
                        ANSWER += input;
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void handleRequest(BufferedReader in, Socket sock){
        // ------------ READ THROUGH HEADERS AND DETERMINE ACTIONS ------------
        try{
            String line = in.readLine();
            while(line != null && line.length() > 0){
                String method = line.split(" ")[0];
                String url = line.split(" ")[1];
                if(method.equals("GET")){
                    if(url.equals("/favicon.ico")){ //IGNORE FAVICON REQUESTS...
                        break;
                    }else if(url.equals("/0")){
                        //System.out.println("0");
                        handleInput("0");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/1")){
                        //System.out.println("1");
                        handleInput("1");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/2")){
                        //System.out.println("2");
                        handleInput("2");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/3")){
                        //System.out.println("3");
                        handleInput("3");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/4")){
                        //System.out.println("4");
                        handleInput("4");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/5")){
                        //System.out.println("5");
                        handleInput("5");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/6")){
                        //System.out.println("6");
                        handleInput("6");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/7")){
                        //System.out.println("7");
                        handleInput("7");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/8")){
                        //System.out.println("8");
                        handleInput("8");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/9")){
                        //System.out.println("9");
                        handleInput("9");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/add")){
                        //System.out.println("add");
                        handleInput("add");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/sub")){
                        //System.out.println("sub");
                        handleInput("sub");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/mult")){
                        //System.out.println("mult");
                        handleInput("mult");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/div")){
                        //System.out.println("div");
                        handleInput("div");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/")){
                        //System.out.println("RESET");
                        ANSWER = "";
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/mem")){
                        //System.out.println("mem");
                        handleInput("mem");
                        serveWebsite(sock);
                        break;
                    }else if(url.equals("/eq")){
                        //System.out.println("equals");
                        ANSWER = calcAnswer();
                        answerCalculator1 = new String[0];
                        answerCalculator2 = new String[0];
                        serveWebsite(sock);
                        break;
                    }
                    //System.out.println(line);
                    line = in.readLine();
                }else if(method.equals("HEAD")){
                    // SEND SERVER INFORMATION IN HEADERS
                    handleHead(sock);
                    break;
                }else if(method.equals("OPTIONS")){
                    // SEND AVAILABLE HTTP METHODS IN HEADERS
                    handleOptions(sock);
                    break;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        // --------------------------------------------------------------------
    }

    //helper function for calcAnswer
    private static void addToArray1(String add){
        String[] temp = answerCalculator1;
        int length = answerCalculator1.length;
        answerCalculator1 = new String[length+1];
        for(int i = 0; i < temp.length; i++){
            answerCalculator1[i] = temp[i];
        }
        answerCalculator1[answerCalculator1.length-1] = add;
    }
    private static void addToArray2(String add){
        String[] temp = answerCalculator2;
        int length = answerCalculator2.length;
        answerCalculator2 = new String[length+1];
        for(int i = 0; i < temp.length; i++){
            answerCalculator2[i] = temp[i];
        }
        answerCalculator2[answerCalculator2.length-1] = add;
    }
    private static void calcValue(int index){
        int leftNumber = Integer.parseInt(answerCalculator1[index-1]);
        String operant = answerCalculator1[index];
        int rightNumber = Integer.parseInt(answerCalculator1[index+1]);
        int ans = 0;

        if(operant.equals("/")){
            ans = leftNumber / rightNumber;
        }else if(operant.equals("x")){
            ans = leftNumber * rightNumber;
        }else if(operant.equals("-")){
            ans = leftNumber - rightNumber;
        }else{
            ans = leftNumber + rightNumber;
        }
        addToArray2(String.valueOf(ans));

        //loop through the rest of the array and add all the other values to array 2 to do another run through
        if(index+2 < answerCalculator1.length){
            for(int i = index+2; i < answerCalculator1.length; i++){
                addToArray2(answerCalculator1[i]);
            }
        }
    }

    private static String calcAnswer(){
        // ------------ CONVERT STRING EQUATION INTO ANSWER ------------
        String numberTracker = "";
        String substringHolder = "";
        boolean finished = true;

        //System.out.println(ANSWER);

        if(ANSWER.length() >= 2 && isOperant(ANSWER.substring(ANSWER.length()-1))){
            if(isOperant(ANSWER.substring(ANSWER.length()-2, ANSWER.length()-1))){
                ANSWER = ANSWER.substring(0, ANSWER.length()-2);
            }else{
                ANSWER = ANSWER.substring(0, ANSWER.length()-1);  
            }
        }

        //System.out.println(ANSWER);

        //loop to place all the numbers and operants into an array
        for(int i = 0; i < ANSWER.length(); i++){
            substringHolder = ANSWER.substring(i, i+1);
            if(!isOperant(substringHolder)){
                numberTracker += substringHolder;
            }else{
                addToArray1(numberTracker);
                addToArray1(substringHolder);
                numberTracker = "";
            }
        }
        addToArray1(numberTracker);

        /*for(int i = 0; i < answerCalculator1.length; i++){
            System.out.print(answerCalculator1[i]);
            System.out.print(",");
        }
        System.out.println();*/

        //loop through the array doing multiply and devision first
        while(true){
            //set exit condidtion 
            finished = true;

            for(int i = 0; i < answerCalculator1.length; i++){
                if(isOperant(answerCalculator1[i])){
                    if(answerCalculator1[i].equals("/") || answerCalculator1[i].equals("x")){
                        calcValue(i);
                        break;
                    }else{
                        if(i+2 > answerCalculator1.length-1){
                            addToArray2(answerCalculator1[i-1]);
                            addToArray2(answerCalculator1[i]);
                            addToArray2(answerCalculator1[i+1]);
                        }else{
                            addToArray2(answerCalculator1[i-1]);
                            addToArray2(answerCalculator1[i]);
                        }
                    }
                }
            }

            for(int i = 0; i < answerCalculator2.length; i++){
                //System.out.print(answerCalculator2[i]);
                //System.out.print(",");
                if(answerCalculator2[i].equals("/") || answerCalculator2[i].equals("x")){
                    finished = false;
                }
            }
            //System.out.println();
            //swap the two arrays over and run through it again for multi or div
            answerCalculator1 = answerCalculator2;
            answerCalculator2 = new String[0];

            if(finished){
                break;
            }
        }

        //System.out.println("Switching");

        //loop through the array doing the sum and sub
        while(true){
            //set exit condidtion 
            finished = true;

            //check that there are sums and subs
            if(answerCalculator1.length == 1){
                break;
            }

            for(int i = 0; i < answerCalculator1.length; i++){
                if(isOperant(answerCalculator1[i])){
                    if(answerCalculator1[i].equals("-") || answerCalculator1[i].equals("+")){
                        calcValue(i);
                        break;
                    }else{
                        addToArray2(answerCalculator1[i-1]);
                        addToArray2(answerCalculator1[i]);
                    }
                }
            }

            for(int i = 0; i < answerCalculator2.length; i++){
                //System.out.print(answerCalculator2[i]);
                //System.out.print(",");
                if(answerCalculator2[i].equals("+") || answerCalculator2[i].equals("-")){
                    finished = false;
                }
            }
            //System.out.println();

            //swap the two arrays over and run through it again for multi or div
            answerCalculator1 = answerCalculator2;
            answerCalculator2 = new String[0];

            if(finished){
                break;
            }
        }
        // -------------------------------------------------------------
        MEMANS = answerCalculator1[0];
        return answerCalculator1[0];
    }

    private static void handleHead(Socket s){
    	// ----------- SEND HEADERS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Server: HTTP Calculator"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

    private static void handleOptions(Socket s){
    	// ----------- SEND OPTIONS IN HTTP RESPONSE TO CLIENT -----------
    	String res = 	"HTTP/1.1 200 OK"+"\n"+
    					"Allow: GET HEAD OPTIONS"+"\n"+
    					"Server: HTTP Calculator"+"\n"+
    					"Host: localhost:"+port+"\n"+
    					"X-Powered-By: Java\n\n";
        try{
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.print(res);
            out.flush();
        }catch(Exception e){
            e.printStackTrace();
        }
        // ---------------------------------------------------------------
    }

}
















