/**
 * demonstrates a simple graphql query and subscription from the client-side perspective.
 *
 * @author Josh Long
 */
import {createClient} from 'graphql-ws';

window.addEventListener('load', function () {


    const client = createClient({
        url: 'ws://localhost:8080/graphql',
    });

    // subscriptions
    client.subscribe(
        {
            query: 'subscription { newCustomers {  id , name  } }',
        },
        {
            next: (result) => {
                console.log('new file data!', result.data['newCustomers']);
            },
            error: (error) => {
                console.log('oops! we got an error!');
            },
            complete: (done) => {
                console.log("we're done!");
            },
        },
    );

});

