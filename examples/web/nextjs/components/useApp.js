// :snippet-start: use-app-hook
import { useEffect, useState } from "react";
import * as Realm from "realm-web";

function useApp() {
  const [app, setApp] = useState(null);
  // Run in useEffect so that App is not created in server-side environment
  useEffect(() => {
    setApp(Realm.getApp(process.env.NEXT_PUBLIC_APP_ID));
  }, []);
  return app;
}

export default useApp;
// :snippet-end:
