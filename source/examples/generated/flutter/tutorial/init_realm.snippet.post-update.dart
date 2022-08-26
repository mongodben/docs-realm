final userItemSub =
    realm.subscriptions.findByName('getUserItemsWithHighOrNoPriority');
if (userItemSub == null) {
  realm.subscriptions.update((mutableSubscriptions) {
    // server-side rules ensure user only downloads own items
    mutableSubscriptions.add(
        realm.query<Item>(
            'priority <= \$0 OR priority == nil', [PriorityLevel.high]),
        name: 'getUserItemsWithHighOrNoPriority');
  });
}