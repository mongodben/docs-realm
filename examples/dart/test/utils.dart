import 'dart:io';

import 'package:realm_dart/realm.dart';
import 'dart:math';

Future<void> cleanUpRealm(Realm realm, [App? app]) async {
  await app?.currentUser?.logOut();
  if (!realm.isClosed) {
    realm.close();
  }
  sleep(Duration(milliseconds: 250));
  Realm.deleteRealm(realm.config.path);
}

final random = Random();
String generateRandomString(int len) {
  const _chars = 'abcdefghjklmnopqrstuvwxuz';
  return List.generate(len, (index) => _chars[random.nextInt(_chars.length)])
      .join();
}
// TODO: remove, just triggering build
