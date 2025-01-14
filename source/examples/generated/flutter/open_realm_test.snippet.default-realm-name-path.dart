Configuration.defaultRealmName = "myRealmName.realm";

String customDefaultRealmPath = path.join(
    (await Directory.systemTemp.createTemp()).path,
    Configuration.defaultRealmName);
Configuration.defaultRealmPath = customDefaultRealmPath;

// Configurations used in the application will use these values
Configuration config = Configuration.local([Car.schema]);
// The path is your system's temp directory
// with the file named 'myRealmName.realm'
print(config.path);
