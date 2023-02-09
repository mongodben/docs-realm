// :snippet-start: link-identities
import React from 'react';
import {useUser} from '@realm/react';
import Realm from 'realm';
// :remove-start:
import {useEffect} from 'react';
import {AppProvider, UserProvider, useApp} from '@realm/react';
import {render, fireEvent, waitFor} from '@testing-library/react-native';
import {View, Button} from 'react-native';

const APP_ID = 'example-testers-kvjdy';
const rand = Date.now().toString();
const userPass = {
  email: `user+link-identities-${rand}@example.com`,
  password: 'abc123',
};

function AppWrapper() {
  return (
    <View>
      <AppProvider id={APP_ID}>
        <UserProvider fallback={<LogIn />}>
          <LinkUserIdentities
            username={userPass.email}
            password={userPass.password}
          />
        </UserProvider>
      </AppProvider>
    </View>
  );
}

function LogIn() {
  const app = useApp();

  useEffect(() => {
    app.logIn(Realm.Credentials.anonymous());
  }, []);
  return <></>;
}
let higherScopeUser: Realm.User;
// :remove-end:

interface LinkUserIdentitiesProps {
  username: string;
  password: string;
}

function LinkUserIdentities({username, password}: LinkUserIdentitiesProps) {
  const user = useUser();

  const linkIdentities = async (credentials: Realm.Credentials) => {
    await user.linkCredentials(credentials);
    higherScopeUser = user!; // :remove:
  };

  // ...
  // :remove-start:
  return (
    <Button
      onPress={() =>
        linkIdentities(Realm.Credentials.emailPassword(username, password))
      }
      testID='test-link-identities' // :remove:
      title='Link Credentials'
    />
  );
  // :remove-end:
}
// :snippet-end:
beforeEach(async () => {
  try {
    await Realm.App.getApp(APP_ID).emailPasswordAuth.registerUser(userPass);
  } catch (err) {
    console.log(err);
  }
});
afterEach(async () => {
  const app = await Realm.App.getApp(APP_ID);
  app.currentUser
    ? await app.deleteUser(app.currentUser)
    : console.log('no current user');
});

test('Use MongoDB Data Access', async () => {
  const {getByTestId} = render(<AppWrapper />);

  const button = await waitFor(() => getByTestId('test-link-identities'));
  fireEvent.press(button);
  await waitFor(() => expect(higherScopeUser.identities.length).toBe(2));
});