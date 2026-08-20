public enum Command {
    BYE, LIST, MARK, UNMARK, TODO, DEADLINE, EVENT, DELETE;

    public static Command fromWord(String word) throws EveException {
        try {
            return Command.valueOf(word.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new EveException("OOPS!!! I'm sorry, but I don't know what that means :-(");
        }
    }
}
