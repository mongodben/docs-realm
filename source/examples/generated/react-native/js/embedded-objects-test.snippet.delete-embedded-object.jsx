const ContactInfo = ({contactName}) => {
  const contacts = useQuery(Contact);
  const toDelete = contacts.filtered(`name == '${contactName}'`)[0];
  const realm = useRealm();

  const deleteContact = () => {
    realm.write(() => {
      // Deleting the contact also deletes the embedded address of that contact
      realm.delete(toDelete);
    });
  };
  return (
    <View>
      <Text testID='contactNameText'>{contactName}</Text>
      <Button
        testID='deleteContactBtn'
        onPress={deleteContact}
        title='Delete Contact'
      />
    </View>
  );
};
