import TestRenderer from "react-test-renderer";
import { render, waitFor, screen } from "@testing-library/react";
import { MockedProvider } from "@apollo/client/testing";
import React, { useEffect, useState } from "react";
import * as Realm from "realm-web";
import {
  ApolloClient,
  ApolloProvider,
  HttpLink,
  InMemoryCache,
} from "@apollo/client";
// :snippet-start: import-dependencies-query
// import whichever Apollo hooks you're using
import { useQuery, useMutation } from "@apollo/client";
import gql from "graphql-tag";
// :snippet-end:
import { GRAPHQL_APP_ID, GRAPHQL_ENDPOINT } from "../realm.config.json";

// :snippet-start: run-query

const ALL_MOVIES = gql`
  query AllMovies {
    movies {
      _id
      title
      year
      runtime
    }
  }
`;
// Must be rendered inside of an ApolloProvider
function Movies() {
  const { loading, error, data } = useQuery(ALL_MOVIES);
  if (loading) {
    return <div>loading</div>;
  }
  if (error) {
    return <div>encountered an error: {error}</div>;
  }
  return <MovieList movies={data.movies} />;
}
// :snippet-end:

// :snippet-start: run-mutation
const UPDATE_MOVIE_TITLE = gql`
  mutation UpdateMovieTitle($oldTitle: String!, $newTitle: String!) {
    updateOneMovie(query: { title: $oldTitle }, set: { title: $newTitle }) {
      title
      year
    }
  }
`;

// Must be rendered inside of an ApolloProvider
function MovieList({ movies }) {
  const [updateMovieTitle] = useMutation(UPDATE_MOVIE_TITLE);
  return (
    <ul>
      {movies.map((movie) => (
        <li key={movie._id}>
          <div>{movie.title}</div>
          <button
            onClick={() => {
              updateMovieTitle({
                variables: {
                  oldTitle: movie.title,
                  newTitle: "Some New Title",
                },
              });
            }}
          >
            Update Title
          </button>
        </li>
      ))}
    </ul>
  );
}
// :snippet-end:

// :snippet-start: paginate

const PAGINATE_MOVIES = gql`
  query PaginateMovies(
    $prevTitle: String
    $nextTitle: String
    $limit: Int!
    $sortDirection: MovieSortByInput!
  ) {
    movies(
      query: { title_gt: $prevTitle, title_lt: $nextTitle } # can add other query filters here
      limit: $limit
      sortBy: $sortDirection
    ) {
      title
      year
      director
    }
  }
`;

const resultsPerPage = 5;

function PaginateMovies() {
  const initVars = {
    prevTitle: undefined,
    nextTitle: undefined,
    limit: resultsPerPage,
    sortDirection: "TITLE_ASC",
  };
  const [variables, setVariables] = useState(initVars);
  const [firstTitle, setFirstTitle] = useState();
  const { data, error, loading } = useQuery(PAGINATE_MOVIES, {
    variables: initVars,
  });
  const [pagePreviousDisabled, setPagePreviousDisabled] = useState(true);
  const [pageNextDisabled, setPageNextDisabled] = useState(false);

  useEffect(() => {
    if (data.length && firstTitle === undefined) {
      setFirstTitle(data.movies[0].title);
      setPagePreviousDisabled(false);
    }
    if (data.movies.length < resultsPerPage) {
      setPageNextDisabled(true);
    }
    if (
      variables.prevTitle === undefined ||
      data?.movies[0]?.title === firstTitle
    ) {
      setPagePreviousDisabled(true);
    }
  }, [data, data?.movies?.length, firstTitle, variables.prevTitle]);
  if (loading) {
    console.log("LOADING::", loading);
    return <div>loading</div>;
  }
  if (error) {
    console.log("ERR::", error);
    return <div>encountered an error: {error.message}</div>;
  }

  function goToNextPage() {
    setVariables({
      nextTitle: undefined,
      prevTitle: data.movies[data.movies.length - 1].title,
      limit: resultsPerPage,
      sortDirection: "TITLE_ASC",
    });
  }

  function goToPrevPage() {
    setVariables({
      nextTitle: data.movies[0].title,
      prevTitle: undefined,
      limit: resultsPerPage,
      sortDirection: "TITLE_DESC",
    });
  }
  console.log("DATA::", data);
  return (
    <div>
      <h1>Movies</h1>
      {/* {data.map((movie) => (
        <div key={movie.title}>
          <h3>{movie.title}</h3>
          <p>Director: {" " + movie.director}</p>
          <p>Year Published: {" " + movie.year}</p>
          <br />
        </div>
      ))} */}
      <div>
        <button disabled={pagePreviousDisabled} onClick={goToPrevPage}>
          &larr; Previous Page
        </button>
        <button disabled={pageNextDisabled} onClick={goToNextPage}>
          Next Page &rarr;
        </button>
      </div>
    </div>
  );
}

// :snippet-end:
const movies = [
  {
    _id: 1,
    title: "Saving Private Ryan",
    year: 1997,
    runtime: 169,
  },
  {
    _id: 2,
    title: "Defiance",
    year: 2008,
    runtime: 137,
  },
  {
    _id: 3,
    title: "Dunkirk",
    year: 2017,
    runtime: 106,
  },
  {
    _id: 4,
    title: "Operation Mincemeat",
    year: 2021,
    runtime: 128,
  },
  {
    _id: 5,
    title: "The Imitation Game",
    year: 2014,
    runtime: 114,
  },
  {
    _id: 6,
    title: "The Imitation Game",
    year: 2014,
    runtime: 114,
  },
  {
    _id: 7,
    title: "Valkyrie",
    year: 2008,
    runtime: 124,
  },
  {
    _id: 8,
    title: "Enemy at the Gates",
    year: 2001,
    runtime: 131,
  },
];
const app = new Realm.App(GRAPHQL_APP_ID);
beforeAll(async () => {
  const user = await app.logIn(Realm.Credentials.anonymous());
  await user
    .mongoClient("mongodb-atlas")
    .db("example")
    .collection("movies")
    .deleteMany({
      _id: {
        $in: movies.map((movie) => movie._id),
      },
    });
  await user
    .mongoClient("mongodb-atlas")
    .db("example")
    .collection("movies")
    .insertMany(movies);
});
afterAll(async () => {
  const user = await app.logIn(Realm.Credentials.anonymous());
  await user
    .mongoClient("mongodb-atlas")
    .db("example")
    .collection("movies")
    .deleteMany({
      _id: {
        $in: movies.map((movie) => movie._id),
      },
    });
  await user.logOut();
});

describe("Queries and mutations", () => {
  let clicked = false;
  const mocks = [
    {
      request: {
        query: ALL_MOVIES,
      },
      result: {
        data: {
          movies,
        },
      },
    },
    {
      request: {
        query: UPDATE_MOVIE_TITLE,
        variables: {
          oldTitle: "Saving Private Ryan",
          newTitle: "Some New Title",
        },
      },
      result: function () {
        clicked = true;
        return {
          data: {
            updateOneMovie: {
              title: "Some New Title",
              year: 1997,
            },
          },
        };
      },
    },
  ];

  const component = TestRenderer.create(
    <MockedProvider mocks={mocks} addTypename={false}>
      <Movies />
    </MockedProvider>
  );
  it("Run a query", async () => {
    const tree = component.toJSON();
    expect(tree.children).toContain("loading");
    await new Promise((resolve) => setTimeout(resolve, 10));
    const divs = await component.root.findAllByType("div");
    expect(divs.length).toBe(8);
  });

  it("Run a mutation", async () => {
    await TestRenderer.act(async () => {
      const buttons = await component.root.findAllByType("button");
      buttons[0].props.onClick();
    });
    await new Promise((resolve) => setTimeout(resolve, 10));
    expect(clicked).toBe(true);
  });

  fit("Paginate results", async () => {
    // Connect to your MongoDB Realm app
    const app = new Realm.App(GRAPHQL_APP_ID);

    // Configure the ApolloClient to connect to your app's GraphQL endpoint
    const client = new ApolloClient({
      link: new HttpLink({
        uri: GRAPHQL_ENDPOINT,
        headers: {
          Authorization: `Bearer ${app.currentUser.accessToken}`,
        },
      }),
      cache: new InMemoryCache(),
    });
    const AppWithApollo = () => (
      <ApolloProvider client={client}>
        <PaginateMovies />
      </ApolloProvider>
    );

    render(<AppWithApollo />);
    await waitFor(
      async () => {
        const f = await screen.findByText("Movies");
      },

      { timeout: 3000 }
    );
  });
});
