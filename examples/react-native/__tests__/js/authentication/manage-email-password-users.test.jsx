// :snippet-start: register-user
import React from 'react';
import {useApp, UserProvider, AppProvider} from '@realm/react';
import Realm from 'realm';
// :remove-start:
import {render, fireEvent, waitFor} from '@testing-library/react-native';
import {View, Button, Text} from 'react-native';

const APP_ID = 'example-testers-kvjdy';

const rand = Date.now();
const userCreds = {
  username: `user+email-pw-${rand}@example.com`,
  password: 'abc123',
};
let higherScopedUser;
// :remove-end:

function AppWrapper() {
  return (
    <View>
      <AppProvider id={APP_ID}>
        <RegisterUser />
        <UserProvider fallback={LogIn}>
          {/* ...Other components in app that require authentication */}
        </UserProvider>
      </AppProvider>
    </View>
  );
}

function RegisterUser() {
  const app = useApp();

  async function register(email, password) {
    // Register new email/password user
    await app.emailPasswordAuth.registerUser({email, password});
    // Log in the email/password user
    await app.logIn(Realm.Credentials.emailPassword(email, password));
    higherScopedUser = app.currentUser; // :remove:
  }
  // ...
  // :remove-start:
  return (
    <Button
      onPress={() => register(userCreds.username, userCreds.password)}
      testID='test-register-user'
      title='Test Me!'
    />
  );
  // :remove-end:
}
// :snippet-end:

function LogIn() {
  return <Text>Dummy component</Text>;
}

afterEach(async () => {
  const app = Realm.App.getApp(APP_ID);
  await app.logIn(
    Realm.Credentials.emailPassword(userCreds.username, userCreds.password),
  );
  await app.deleteUser(app.currentUser);
  await app.currentUser?.logOut();
});

test('Register user', async () => {
  const {getByTestId} = render(<AppWrapper />);
  const button = await waitFor(() => getByTestId('test-register-user'));
  await fireEvent.press(button);
  await waitFor(() => {
    expect(higherScopedUser.identities[0].providerType).toBe('local-userpass');
  });
});
