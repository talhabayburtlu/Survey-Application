import React from 'react';
import ReactDOM from 'react-dom';

import {Provider} from "react-redux"
import {applyMiddleware, combineReducers, compose, createStore} from "redux"
import {BrowserRouter} from "react-router-dom";
import thunk from "redux-thunk";

import App from './App';
import authReducer from "./store/reducers/auth"

const rootReducer = combineReducers({
    auth: authReducer
});

const store = createStore(rootReducer, compose(
    applyMiddleware(thunk)
));

ReactDOM.render(
    <BrowserRouter>
        <Provider store={store}>
            <App/>
        </Provider>
    </BrowserRouter>,
    document.getElementById('root')
);
