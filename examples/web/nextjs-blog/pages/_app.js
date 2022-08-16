// :snippet-start: custom-app-wrapper
import * as Realm from "realm-web";
// :remove-start:
import Layout from "../components/layout";
// :remove-end:
import AppServicesContext from "../realm/AppServicesContext";

function MyApp({ Component, pageProps }) {
  const appServices = new Realm.App(process.env.NEXT_PUBLIC_APP_ID);

  return (
    <AppServicesContext.Provider value={appServices}>
      {/* :remove-start:*/}
      <Layout>
        {/* :remove-end: */}
        <Component {...pageProps} />
        {/* :remove-start:*/}
      </Layout>
      {/* :remove-end: */}
    </AppServicesContext.Provider>
  );
}

export default MyApp;
// :snippet-end:
