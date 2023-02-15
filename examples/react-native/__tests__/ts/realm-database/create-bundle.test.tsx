// :snippet-start: create-bundle
import Realm from 'realm';
import Cat from '../Models/Cat';
let higherScopedConfig: Realm.Configuration; // :remove:

async function createBundle() {
  const config = {
    schema: [Cat.schema],
    path: 'bundle.realm',
  };
  higherScopedConfig = config; // :remove:
  const realm = await Realm.open(config);

  // add data to realm
  realm.write(() => {
    realm.create('Cat', {name: 'Jasper', birthday: 'Nov 2, 2000'});
    realm.create('Cat', {name: 'Maggie', birthday: 'December 4, 2007'});
    realm.create('Cat', {name: 'Sophie', birthday: 'July 10, 2019'});
  });

  realm.close();
}
// :uncomment-start:
// createBundle();
// :uncomment-end:
// :snippet-end:
beforeEach(() => {
  Realm.deleteFile(higherScopedConfig);
});
test('create bundle', async () => {
  await createBundle();
  const realm = await Realm.open(higherScopedConfig);
  expect(realm.objects('Cat').length).toBe(3);
  realm.close();
  // Note: not deleting realm in clean up b/c using in `bundled.test.tsx`
});
