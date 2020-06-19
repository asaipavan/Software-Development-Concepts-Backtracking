
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FillInPuzzle {
	static int size=10;
	static char[][] matrix=new char[10][10];
	static char[][] crossword=new char[10][10];
	static String[] words=new String[3];
	static final int[] rowFlag = { 0, 1 };
	static final int[] columnFlag = { 1, 0 };
	static int choices=0;
	//loads the grid and no of words to solve the puzzle from Bufferedreader
	Boolean loadPuzzle(BufferedReader reader)
	{


		String read;

		//storing the data from the Buffered Reader into the data ArrayList

		ArrayList<String> data=new ArrayList<String>();
		try {
			while ((read = reader.readLine()) != null) {
				data.add(read);
			}

			String[] matrixSize=data.get(0).split(" ");
			int columns=Integer.parseInt(matrixSize[0]);
			int rows=Integer.parseInt(matrixSize[1]);
			int noofwords=Integer.parseInt(matrixSize[2]);
			words=new String[noofwords];
			for(int j=0;j<noofwords;j++)
			{
				words[j]=data.get(data.size()-noofwords+j);
			}

			if(rows>columns)
			{
				size=rows;
			}
			else
			{
				size=columns;
			}
			//creating the matrix with based on size and assigning it with *
			matrix=new char[size][size];
			crossword=new char[size][size];
			for(int k=0;k<size;k++)
			{
				for(int l=0;l<size;l++)
				{
					matrix[k][l]='*';
				}
			}
			//assigning wordspaces that needs to be filled in with #
			for(int r=1;r<(data.size()-noofwords);r++)
			{
				String [] wordspaces=data.get(r).split(" ");
				int rowIndex=Integer.parseInt(wordspaces[1]);
				int colIndex=Integer.parseInt(wordspaces[0]);
				int wordlength=Integer.parseInt(wordspaces[2]);
				if(wordspaces[3].equals("h"))
				{
					int count=0;
					for(int m=colIndex,n=rowIndex;count!=wordlength&&n<rows;m++)
					{
						matrix[n][m]='#';
						count++;
					}
				}
				else
				{
					int count=0;
					for(int m=colIndex,n=rowIndex;m<columns&&count!=wordlength;n--)
					{
						matrix[n][m]='#';
						count++;
					}
				}
			}
			for(int col = 0;col < matrix[0].length; col++){
				for(int row = 0; row < matrix.length/2; row++) {
					char temp = matrix[row][col];
					matrix[row][col] = matrix[matrix.length - row - 1][col];
					matrix[matrix.length - row - 1][col] = temp;
				}
			}

		} 
		catch (IOException e) {
			return false;
		}
		return true;

	}

	//solve method will solve the crossword puzzle using backtracking algorithm and returns the value


	Boolean solve()
	{
		char[][] crossword=new char[size][size];
		try
		{
			// calling solveCrossword to solve puzzle
			crossword=solveCrossword(matrix, Arrays.stream(words).collect(Collectors.toSet()), 0, 0, 0);
		}
		catch(Exception e)
		{
			return false;
		}
		if(crossword.length==0)
		{
			return false;
		}
		return true;
	}

	//solveCrossword solves the puzzle by passing the matrix and set of words
	char[][] solveCrossword(char[][] matrix, Set<String> ArrayWords, int rowIndex, int columnIndex, int direction) {
		if (rowIndex == size) {
			return matrix;
		}
		if (columnIndex == size) {
			return solveCrossword(matrix, ArrayWords, rowIndex + 1, 0, 0);
		}
		if (direction == rowFlag.length) {
			return solveCrossword(matrix, ArrayWords, rowIndex, columnIndex + 1, 0);
		}

		int length = 0;
		int prRow = rowIndex - rowFlag[direction];
		int prCol = columnIndex- columnFlag[direction];
		if (prRow >= 0 && prCol < size && prRow < size && prCol >= 0  && matrix[prRow][prCol] != '*') {
			length = 0;
		}

		while (columnIndex < size && rowIndex >= 0 && rowIndex < size && columnIndex >= 0 && matrix[rowIndex][columnIndex] != '*') {
			length++;

			rowIndex =rowIndex + rowFlag[direction];
			columnIndex = columnIndex + columnFlag[direction];
		}
		
		if (length > 1) {
			for (String remainWord : new ArrayList<>(ArrayWords)) {
				if (insert(matrix, rowIndex, columnIndex, direction, length, remainWord)) {
					List<Integer> insertflag = new ArrayList<Integer>();

					for (int i = 0; i < length; i++) {
						int insRow = rowIndex + rowFlag[direction] * i;
						int insCol = columnIndex + columnFlag[direction] * i;

						if (matrix[insRow][insCol] == '#') {
							matrix[insRow][insCol] = remainWord.charAt(i);

							insertflag.add(i);
						}
					}
					choices++;
					ArrayWords.remove(remainWord);

					char[][] result = solveCrossword(matrix, ArrayWords, rowIndex, columnIndex, direction + 1);
					if (result != null) {
						return result;
					}


					ArrayWords.add(remainWord);
					for (int k : insertflag) {
						int insRow = rowIndex + rowFlag[direction] * k;
						int insCol = columnIndex + columnFlag[direction] * k;

						matrix[insRow][insCol] = '#';
					}
				}
			}
			return null;
		} else {

			return solveCrossword(matrix, ArrayWords, rowIndex, columnIndex, direction + 1);

		}
	}
		

	//return boolean value based on whether string can be inserted at the position or not 

	boolean insert(char[][] matrix, int r, int c, int direction, int insertLength, String word) {
		return word.length() == insertLength && IntStream.range(0, word.length()).allMatch(flag -> {
			int insRow = r + rowFlag[direction] * flag;
			int insCol = c + columnFlag[direction] * flag;

			return matrix[insRow][insCol] == '#' || matrix[insRow][insCol] == word.charAt(flag);
		});
	}



	//prints the output to outstream with the help of printwriter
	void print(PrintWriter outstream)
	{
		for(int k=0;k<size;k++) { for(int l=0;l<size;l++) {
			if(matrix[k][l]=='*')
			{
				outstream.print(" ");
			}
			else
			{
				outstream.print(matrix[k][l]);
			}
		}
		outstream.println();
		}
		outstream.flush();
	}

	//it returns the number of guesses that program had to make and undo while solving the puzzle

	int choices()
	{
		return choices;

	}
}
