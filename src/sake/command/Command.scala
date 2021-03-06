package sake.command

import sake.util._

class Command[A,B](val name: String, val defaultOptions: Option[Map[A,B]]) {
    
    def this(name:String, defaultOpts: Map[A,B]) = this(name, Some(defaultOpts))
    def this(name:String, defaultOpt0: (A,B), defaultOpts: (A,B)*) = 
        this(name, Map[A,B](defaultOpt0)++defaultOpts)
    def this(name:String) = this(name, None)
    
    /**
     * The user specified options will be checked to confirm that any required options are present.
     * Override as needed in subclasses.
     */
    val requiredOptions: List[A] = Nil
    
    /**
     * Final processing of the options before invoking "action", _e.g.,_ to handle option 
     * "aliases" or to verify the _values_ for certain _keys_ are valid.
     * Override as needed in subclasses.
     */
    protected def optionsPostFilter(options: Map[A,B]) = options

    /**
     * This is the method that does the normal work of the command.
     * @return a Passed on success or Failed, otherwise.
     */
    protected def action(options: Map[A,B]):Result = new Passed()

    /**
     * Perhaps to recover from an error. Users can post-process the result using the 
     * "command(...) and { result => ... }" idiom. 
     * Override as needed in subclasses.
     */
    protected def postFilterResult(result: Result) = result

    def apply(options: (A,B)*): Result = {
        try {
            val opts = filterOptions(optionsToMap(options))
            val result = postFilterResult(action(opts))
            result match {
                case s:Passed[_] => s.message match {
                    case None =>
                    case Some(msg) => Log.log(Level.Info, msg)
                }
                case f:Failed[_] => Exit.error("command \""+name+"\" failed with result: "+f)
            }
            result
        } catch {
            case be:BuildError => throw be
            case th:Throwable => Exit.error("command \""+name+"\" failed with exception: "+th,th)
        }
    }
    
    private def optionsToMap(options: Seq[(A,B)]) = {
        defaultOptions match {
            case Some(map) => map ++ options
            case None => Map() ++ options
        }
    }
    
    private def filterOptions(options: Map[A,B]) = {
        checkForMissingRequiredOptions(options)
        optionsPostFilter(options)
    }
    
    private def checkForMissingRequiredOptions(options: Map[A,B]):Unit = {
        val missingOptions = for { 
            key <- requiredOptions
            if (! options.contains(key)) 
        } yield key
        if (missingOptions.length > 0)
            Exit.error(name+" requires option(s) "+missingOptions)
    }
}

