function GraphQLProvider({ children }) {
  const app = Realm.App.getApp(process.env.NEXT_PUBLIC_APP_ID);
  const client = new ApolloClient({
    link: new HttpLink({
      uri: process.env.NEXT_PUBLIC_GRAPHQL_API_ENDPOINT,
      // We get the latest access token on each request
      fetch: async (uri, options) => {
        const accessToken = app?.currentUser?.accessToken;
        options.headers.Authorization = `Bearer ${accessToken}`;
        return fetch(uri, options);
      },
    }),
    cache: new InMemoryCache(),
  });
  return <ApolloProvider client={client}>{children}</ApolloProvider>;
}
