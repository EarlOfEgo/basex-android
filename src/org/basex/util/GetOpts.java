package org.basex.util;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This class splits the input String into its arguments and checks if 
 * there is a path expression.
 * 
 * @author Workgroup DBIS, University of Konstanz 2005-08, ISC License
 * @author Hannes Schwarz - Hannes.Schwarz@gmail.com
 * @version 0.1
 */
public final class GetOpts {

  /** Argument of an option is stored here. */
  private String optarg;

  /** The path expression is stored here. */
  private String path;

  /** Index of option to be checked. */
  private int optindex;

  /** Index of option to be checked. */
  private int multipleOptIndex;

  /** The valid short options. */
  private String optString;
  
  /** Arguments found. */ 
  private ArrayList<String> foundArgs;  

  /** Arguments passed to the program. */ 
  private String[] args;

  /** The variable optopt saves the last known option 
   * character returned by getopt(). */
  private int optopt = 0;

  /** optreset must be set to 1 before the second and each 
   * additional set of calls to getopt(), and the variable 
   * optindex must be reinitialized. */
  private final int optReset = 1;

  /**
   * Construct a basic Getopt instance with the given input data.
   * 
   * @param arguments The String passed from the command line 
   *             
   * @param options A String containing a description of the 
   *                  valid options
   */
  public GetOpts(final String arguments, final String options) {
    this.args = arguments.split(" ");
    this.optString = options;    
    this.optindex = 0;
    this.optarg = null;
    this.path = null;
    this.multipleOptIndex = 1;
    this.foundArgs = new ArrayList<String>();
  }
  /**
   * Construct a basic Getopt instance with the given input data.
   * 
   * @param arguments The String passed from the command line
   * @param options A String containing a description of the 
   *                  valid options
   * @param firstOptionIndex start scanning at this index                  
   */
  public GetOpts(final String arguments, final String options,
      final int firstOptionIndex) {
    this.args = arguments.split(" ");
    this.optString = options;    
    this.optindex = firstOptionIndex;
    this.optarg = null;
    this.path = null;
    this.multipleOptIndex = 1;
    this.foundArgs = new ArrayList<String>();
  }
  /**
   * Getter of the index.
   * 
   * @return optindex - Index of the next option to be checked. Returns
   *                    -1 if it is at the end of the optString.      
   */
  public int getOptind() {
    return optindex;
  }

  /** 
   * For communication to the caller. No set method 
   * is provided because setting this variable has no effect.
   * 
   * @return When an option is found it is stored in optarg
   * and returned here.   
   */
  public String getOptarg() {
    return optarg;
  }

  /**
   * getFoundArgs is used to return all parsed
   * arguments like source_file target_file.
   * 
   * @return all parsed arguments
   */
  public ArrayList<String> getFoundArgs() {
    return foundArgs;
  }
  
  /**
   * getPath is used to store a path expression.
   * 
   * @return path to go
   */
  public String getPath() {
    return path;
  }


  /**
   * This method checks the string passed from the command line. 
   * 
   * If an option is found it returns it and store possible arguments
   * in optarg. If an invalid option is found, a '?' is returned and an
   * error thrown. If there is no more to be checked -1 will be returned.
   * 
   * TODO<HS>: ERROR CODE -> EXCEPTION ?</HS>
   *  
   * @return see above
   * @throws IOException - in case of problems with the PrintOutput
   */
  public int getopt() throws IOException {

    optarg = null;    
    // parsed all input
    if (optindex >= args.length) {
      return -1;
    }

    String arg = args[optindex];
    int argLength = arg.length();
    // option found
    if(arg.startsWith("-") && argLength > 1) {
      int optPos = optString.indexOf(
          args[optindex].charAt(multipleOptIndex)
      );      
      // valid option ? 
      if(optPos > -1) {
        if(optString.length() - 1  > optPos && 
            optString.charAt(optPos + 1) == ':') {
          //argument required
          optopt = args[optindex].charAt(multipleOptIndex);         
          if(argLength > multipleOptIndex + 1) {
            // e.g. -oArgument
            optarg = arg.substring(multipleOptIndex + 1, argLength);
            ++optindex;            
          } else {            
            if(args.length > optindex + 1) {
              optindex += 2;
              optarg = args[optindex - 1];
            } else {
              optindex += 2;
              optarg = ":";
            }
          }
          return optopt;
          // if(optString.charAt(optPos + 2) == ':')
          // not yet implemented - optional argument

        } else {
          /* 
           * no argument allowed if argLength is bigger than 
           * multipleOptIndex + 1 (=2) there must be another 
           * option -> just return this option / increment multipleOptIndex
           *  
           */ 
          if(argLength == multipleOptIndex + 1) {
            ++optindex;
            int tmp = multipleOptIndex;
            multipleOptIndex = 1;
            optopt = args[optindex - 1].charAt(tmp); 
            return optopt;              
          } else {              
            ++multipleOptIndex;            
            optopt = args[optindex].charAt(multipleOptIndex - 1);
            return optopt;
          }

        } 
      } else {
        // Unknown option -> any options left ? set pointer
        if(argLength == multipleOptIndex + 1) { // e.g. -X (X = unknown)
          ++optindex;
          multipleOptIndex = 1;
        } else {  // e.g. -yXa (y,a = known / X = unknown) 
          ++multipleOptIndex;  
        }
        return '?';
      }             
    } else {      
      if(args.length > 0 && args[optindex].length() > 0) {
        // path or nonvalid option
        path = args[optindex];  
        foundArgs.add(args[optindex]);
      }
      if(optindex + 1 == args.length) { 
        // all options parsed
        return -1;
      } else {                
        // return next option
        ++optindex;        
        return getopt();
      }
    }        
  }

  /**
   * In order to use getopt() to evaluate multiple sets of arguments, or to
   * evaluate a single set of arguments multiple times, the variable optreset
   * must be set to 1 before the second and each additional set of calls to
   * getopt(), and the variable optindex must be reinitialized.
   */
  public void reset() {
    optindex = optReset;
  }
}
