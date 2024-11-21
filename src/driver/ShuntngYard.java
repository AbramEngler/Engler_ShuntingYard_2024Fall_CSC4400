package driver;

public class ShuntngYard {
	public static String Operators = "+-*/^()";
	public static String Numbers = "1234567890";
	
	public static boolean IsNumber(String input) {
		for(char c: input.toCharArray()) {
			if (Numbers.indexOf(c) == -1) {
				return false;
			}
		}
		return true;
	}
	
	public static int getPrecedence(String input) {
		char op = input.charAt(0);
		switch (op) {
			case '(':
				return 1;
			case '+':
			case '-':
				return 2;
			case '*':
			case '/':
				return 3;
			case '^':
				return 4;
			case ')':
				return 5;
			default:
				return 0;
		}			
	}

	//parse a math expression into a linked list
    //input: the math expression as a string
    //parsed result will stored in Token list
	public static TokenList ParseFromExp(String exp) {
		TokenList lst = new TokenList(); //doubly linked list
		
		//c could either be a number or an operator
		//if c is an operator, we create a node and append to the list
		//if c is a number, we need to check if the previous char is a number (to make multi-digit numbers)
		//
		String curValue = "";
		for(char c: exp.toCharArray()) 
		{
			if(Operators.indexOf(c) > -1)//this is a valid operator
			{
				if(!curValue.isEmpty())
				{
					Node<String> num = new Node(curValue);
					lst.Append(num);
					curValue = "";
				}
				Node<String> node = new Node(String.format("%c", c)); //same thing as c + ""
				lst.Append(node);
			}
			
			else if(Numbers.indexOf(c) > -1) //this is a number
			{
				curValue += c; //Concatenate
			}
		}
		
		if(!curValue.isEmpty())
		{
			Node<String> num = new Node(curValue);
			lst.Append(num);
		}
		
		return lst;
	}
	
	//take the tokens from Tokens queue, and stored the reversed polish expression in ReversePolish queue
	public static TokenList BuildFromTokens(TokenList tokenList) {
		TokenList outputQueue = new TokenList();
		TokenList opStack = new TokenList();
		
		
		Node<String> token = tokenList.Head;
		tokenList.Remove(token);
		
		while(token != null)
		{
			//do algorithm on this token
			if(IsNumber(token.Payload))
			{
				outputQueue.Enqueue(token);
			}
			else
			{
				int rank = getPrecedence(token.Payload);
				if(rank == 1) //"("
				{
					opStack.Push(token);
				}
				else if (rank == 5) { // ")"
	                Node<String> op = opStack.Peek();
	                while (op != null && !op.Payload.equals("(")) 
	                {
	                    outputQueue.Enqueue(opStack.Pop());
	                    op = opStack.Peek();
	                }
	                opStack.Pop();
				}
				else
				{
					Node<String> op = opStack.Peek();
					while (op != null && getPrecedence(op.Payload) >= rank) 
					{
	                    outputQueue.Enqueue(opStack.Pop());
	                    op = opStack.Peek();
	                }
	                opStack.Push(token);
				}
			}
			//after everything is over
			token = tokenList.Head;
			if(token != null)
			{
			tokenList.Remove(token);
			}	
		 }
		
		 while (opStack.Head != null) 
		 {
		        outputQueue.Enqueue(opStack.Pop());
		 }
		/*
	     * 1.  While there are tokens to be read:
	     * 2.        Read a token
	     * 3.        If it's a number add it to queue
	     * 4.        If it's an operator
	     * 5.               While there's an operator on the top of the stack with greater precedence:
	     * 6.                       Pop operators from the stack onto the output queue
	     * 7.               Push the current operator onto the stack
	     * 8.        If it's a left bracket push it onto the stack
	     * 9.        If it's a right bracket 
	     * 10.            While there's not a left bracket at the top of the stack:
	     * 11.                     Pop operators from the stack onto the output queue.
	     * 12.             Pop the left bracket from the stack and discard it
	     * 13. While there are operators on the stack, pop them to the queue
	     */
		return outputQueue;
	}
	
	//process use the reverse polish format of expression to process the math result
	//output: the math result of the expression
	public static int Process(TokenList queue) {
		//create a temp stack for numbers
		TokenList stack = new TokenList();
		while (!queue.IsEmpty())
		{
			Node<String> token = queue.Dequeue();
			if(IsNumber(token.Payload))
			{
				stack.Push(token);
			}
			else
			{
				Node<String> firstRight = stack.Pop();
				Node<String> secondLeft = stack.Pop();
				int result = Math(secondLeft.Payload, firstRight.Payload, token.Payload);
				Node<String> newNode = new Node<String>(result + "");
				stack.Push(newNode);
			}
		}
		
		return Integer.parseInt(stack.Pop().Payload);
	}
	
	public static int Math(String leftNumber, String rightNumber, String op)
	{
		switch (op) {
			case "+":
				return Integer.parseInt(leftNumber) + Integer.parseInt(rightNumber);
			case "-":
				return Integer.parseInt(leftNumber) - Integer.parseInt(rightNumber);
			case "*":
				return Integer.parseInt(leftNumber) * Integer.parseInt(rightNumber);
			case "/":
			    int right = Integer.parseInt(rightNumber);
			    if (right == 0) {
			        throw new ArithmeticException("Cannot divide by zero");
			    }
			    return Integer.parseInt(leftNumber) / right;
			case "^":
			    return (int) Math.pow(Integer.parseInt(leftNumber), Integer.parseInt(rightNumber));
				
			default:
				return 0;
	}
}
}

