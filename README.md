# BukkitCommands - API komend do silnika Bukkit.

BukkitCommands to API komend dla Bukkit wyciągnięte z biblioteki [Commons](https://github.com/TheMolkaPL/Commons). API idealnie pasuje do dużych i małych projektów ze względu na jego prostotę i zwięzłość kodu. Wszystko opiera się na adnotacji `@CommandInfo` w której to definiuje się wszystkie parametry danej komendy. Biblioteka BukkitCommands wymaga inicjalizacji najlepiej przy włączaniu pluginu. Należy użyć abstrakcyjnej klasy `Commands` do zdefiniowania swojej zmiennej. Implementacją musi być `BukkitCommons`, który odpowiada za wskrzykiwanie komend do silnika serwera oraz tworzenia strony pomocy. W parametrze konstruktora `BukkitCommands` należy podać `Plugin` na którym mają rejestrować się komendy. W tym miejscu należy podać referencję do głównej klasy pluginu.

**Przykładowa inicjalizacja:**

```java
@Override
public void onEnable() {
    Commands commands = new BukkitCommands(this);
}
```

***



## Komenda

Tak jak wspomniałem powyżej wszystkie komendy opierają się o adnotację. Do każdej komendy należy przypisać metodę. Metoda ta powinna być metodą `void` z parametrami `CommandSender` oraz `CommandContext`. `CommandSender` jest obiektem wykonującym komendę [znanym już z API Bukkit'a](https://github.com/Bukkit/Bukkit/blob/master/src/main/java/org/bukkit/command/CommandSender.java). `CommandSender` może być to jednocześnie blokiem komend (command block), konsola lub gracz. Uważaj więc z castowaniem `CommandSender`a do `Player`!

Nazwa metody jest dowolna, aczkolwiek dla dobrej identyfikacji dla programisty powinna zawierać nazwę komendy. Każda metoda musi zostać udekorowana adnotacją `@CommandInfo`. Poniżej przedstawię klucze tych adnotacji, opis oraz przykładowe wartości dla komendy `/teleportto`.

**Specyfikacja @CommandInfo (porządek alfabetyczny):**

| Klucz                               | Opis                                | Przykładowa wartość                 | Dodatkowe                           |
| ----------------------------------- | ----------------------------------- | ----------------------------------- | ----------------------------------- |
| (`String[]`) **name**               | Nazwy (aliasy) komend               | `name = {"teleportto", tpt}`        | **Parametr wymagany**               |
| (`String`) **description**          | Opis komendy                        | `description = "Teleportuj siebie do innego gracza"` | **Zaleca się ustawienie** |
| (`int`) **min**                     | Minimalna ilość argumentów          | `min = 1`                           | Domyślna wartość = 0                |
| (`String[]`) **flags**              | Flagi które mają zostać sparsowane  | `flags = "silent"`                  | Domyślnie puste                     |
| (`String`) **usage**                | Użycie komendy                      | `usage = "[-silent] <player>"`      | Podaje się tylko parametry! Domyślnie puste |
| (`boolean`) **userOnly**            | `false` - komenda także dla konsoli | `userOnly = true`                   | Domyślnie `false`                   |
| (`String`) **permission**           | Uprawnienie do komendy              | `permission = "example.teleporttp"` | Uprawnienia zostaną sprawdzone! Domyślnie puste |
| (`String`) **completer**            | Nazwa metody tab-completera         | `completer = "teleportToCompleter"` | Metoda musi być w tej samej klasie! Domyślnie puste |

Powyższe wartości podają następującą adnotację.

```java
@CommandInfo(
        name = {"teleportto", tpt},
        description = "Teleportuj siebie do innego gracza",
        min = 1,
        flags = "silent",
        usage = "[-silent] <player>",
        userOnly = true,
        permission = "example.teleportto",
        completer = "teleportToCompleter")
public void teleportToCommand(CommandSender sender, CommandContext context) {
    // ...
}
```

***



## CommandContext

`CommandContext` odpowiada za całą komendę oraz podane w niej argumentów. Klasa ta oferuje bogaty system pobierania wartości z flag oraz parametrów różnego typu. Każdą z metod opisze poniżej w tabeli.

**Specyfikacja CommandContext (porządek alfabetyczny):**

| Metoda                                        | Zwraca                                                                                                    |
| --------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| (String[]) **getArgs()**                      | Surowe argumenty komendy (z Bukkit)                                                                       |
| (Command) **getCommand()**                    | `Command` reprezentujące komendę                                                                          |
| (String) **getFlag(String flag)**             | Flaga w komendzie jako String, `null` jeżeli jej brak                                                     |
| (String) **getFlag(String flag, String def)** | Flaga w komendzie jako String, `def` jeżeli jej brak                                                      |
| (boolean) **getFlagBoolean(String flag)**     | Flaga w komendzie jako boolean, `false` jeżeli jej brak lub bład konwersji typu                           |
| (boolean) **getFlagBoolean(String flag, boolean def)** | Flaga w komendzie jako boolean, `def` jeżeli jej brak lub bład konwersji typu                    |
| (double) **getFlagDouble(String flag)**       | Flaga w komendzie jako double, `0.0` jeżeli jej brak lub bład konwersji typu                              |
| (double) **getFlagDouble(String flag, double def)** | Flaga w komendzie jako double, `def` jeżeli jej brak bład konwersji typu                            |
| (int) **getFlagInt(String flag)**             | Flaga w komendzie jako int, `0` jeżeli jej brak lub bład konwersji typu                                   |
| (int) **getFlagInt(String flag, int def)**    | Flaga w komendzie jako int, `def` jeżeli jej brak lub bład konwersji typu                                 |
| (Set<String>) **getFlags()**                  | Set wszystkich sparsowanych flag                                                                          |
| (String) **getLabel()**                       | Dokładna nazwa komendy która została wpisana przez wysyłającego                                           |
| (String) **getParam(int index)**              | Parametr komendy jako String, `null` jeżeli go brak                                                       |
| (String) **getParam(int index, String def)**  | Parametr komendy jako String, `def` jeżeli go brak                                                        |
| (boolean) **getParamBoolean(String flag)**    | Parametr komendy jako boolean, `false` jeżeli go brak lub błąd konwersji typu                             |
| (boolean) **getParamBoolean(String flag, boolean def)** | Parametr komendy jako boolean, `def` jeżeli go brak lub błąd konwersji typu                     |
| (double) **getParamDouble(String flag)**      | Parametr komendy jako double, `0.0` jeżeli go brak lub błąd konwersji typu                                |
| (double) **getParamDouble(String flag, double def)** | Parametr jako int, `def` jeżeli go brak lub błąd konwersji typu                                    |
| (int) **getParamInt(String flag)**            | Parametr komendy jako int, `0` jeżeli go brak lub błąd konwersji typu                                     |
| (int) **getParamInt(String flag, int def)**   | Parametr komendy jako int, `def` jeżeli go brak lub błąd konwersji typu                                   |
| (String) **getParams(int from)**              | Złączone parametry komendy jako String od `from` do końca parametrów, `null` jeżeli ich brak              |
| (String) **getParams(int from, int to)**      | Złączone parametry komendy jako String od `from` do `to`, `null` jeżeli ich brak                          |
| (boolean) **hasFlag(String flag)**            | `true` jeżeli wysyłający podał flagę `flag`                                                               |
| (boolean) **hasFlagValue(String flag)**       | `true` jeżeli wysyłający podał wartość fladze `flag`                                                      |

***



## Wyjątki

Biblioteka dla prostszego wysyłania błędów posiada system wyjątków. Rzucenie wyjątkiem spowoduje zakończenie wykonywania metody oraz wysłanie błędu użytkownikowi. BukkitCommons opiera się o następujące wyjątki.

**Specyfikacja wyjątków (porządek alfabetyczny):**

- **CommandException** - prosty błąd - w parametrze należy podać powód, który zostanie wysłany wysyłającemu.
- **CommandConsoleException** - błędny wysyłający - `true` w parametrze gdy wysyłający jest graczem.
- **CommandPermissionException** - brak uprawnienia - opcjonalna nazwa uprawnienia w parametrze.
- **CommandUsageException** - zbyt mało lub złe argumenty - w parametrze należy podać powód, który zostanie wysłany wysyłającemu.
- **NumberFormatException** - [błędny format liczby](https://docs.oracle.com/javase/7/docs/api/java/lang/NumberFormatException.html)

**Uwaga! Każdy inny wyjątek wyświetli naganny błąd wysyłającemu oraz log w konsoli!**

***



## Flagi

BukkitCommands posiada system flag. Użycie flag opiera się na dwie możliwości; tj. użycie flagi oraz przesłanie wartości poprzez flagę. Flagę tworzy się poprzez porzedzenie jej nazwy myślnikiem - **/example -flag**, na przykład **/tpt -silent Jeb_**. Każdej fladze można nadać wartość poprzez podanie jej w znakach cytatu - **/example -flag "flag value"**. Wartości mogą być oddzielone znakiem odstępu.

Każda flaga musi zostać podana w adnotacji w komendy. W innym przypadku nie zostanie ona sparsowana, a będzie podana jako parametr.

```java
@CommandInfo(
        ...
        flags = {"ip", "uuid"},
        ...)
public void playerInfoCommand(CommandSender sender, CommandContext context) {
    // ...
}
```

Operowanie flagami poprzez pobieranie ich wartości znajduje się w klasie CommandContext podanej jako parametr każdej komendy.

***



## Auto-uzupełnianie

Biblioteka posiada także auto-uzupełnianie argumentów komendy poprzez tabulację. Aby do komendy dodać auto-uzupełnianie wystarczy do adnotacji w **completer** nazwę metody, która zwróci `List<String>` lub `null` oraz za parametry weźmie `CommandSender` oraz `CommandContext`. Trzeba pamiętać o tym, że metoda auto-uzupełniania musi znaleźć się w tej samej klasie co komenda!

```java
@CommandInfo(
        ...
        completer = "pluginInfoCompleter",
        ...
)
public void pluginInfoCommand(CommandSender sender, CommandContext context) {
    // ...
}

public List<String> pluginInfoCompleter(CommandSender sender, CommandContext context) {
    // ...
}
```

***



## Rejestracja

Aby zarejestrować komendę wystarczy w `Commands` użyć `registerCommandObject(...)`, a w parametrze podać obiekt w której ta komenda się znajduje. Jeżeli komenda są statyczne należy użyć metody `registerCommandClass(...)`, natomiast w parametrze podać klasę w której ta komenda się znajduje. Obie możliwości pobiorą wszystkie **możliwe** komendy i zarejestrują je w silniku.

```java
Commands commands = new BukkitCommands(this); // inicjalizacja w onEnable()
commands.registerCommandObject(new ExampleCommands());
```

lub gdy komendy są statyczne

```java
Commands commands = new BukkitCommands(this); // inicjalizacja w onEnable()
commands.registerCommandClass(ExampleCommands.class);
```
