import React, {useEffect} from "react";
import {connect} from 'react-redux'
import {Route, Switch} from "react-router-dom";
import {Redirect, withRouter} from 'react-router'

import Header from "./components/header"
import Login from "./containers/login"
import Register from "./containers/register"
import ChangePassword from "./containers/changePassword"
import Verify from "./containers/verify"
import Results from "./containers/results";
import Submit from "./containers/submit";
import RequestTopic from "./containers/requestTopic";
import ApproveTopics from "./containers/approveTopics";
import ModifyTopics from "./containers/modiftTopics";

const App = (props) => {

    useEffect(() => {
        if (!props.isAuthenticated && (window.location.pathname === "/change_password" && window.location.pathname === "/verify")) {
            props.history.push("/login")
        }
    }, []);

    return (
        <div className="App">
            <Header/>
            <Switch>
                <Route exact path="/login" component={Login}/>
                <Route exact path="/register" component={Register}/>
                <Route exact path="/change_password" component={ChangePassword}/>
                <Route exact path="/verify" component={Verify}/>
                <Route exact path="/results" component={Results}/>
                <Route exact path="/submit" component={Submit}/>,
                <Route exact path="/request" component={RequestTopic}/>
                <Route exact path="/approve" component={ApproveTopics}/>
                <Route exact path="/modify" component={ModifyTopics}/>
                <Redirect to={props.isAuthenticated ? "/" : "/login"}/>
            </Switch>
        </div>
    );
}

const mapStateToProps = state => {
    return {
        isAuthenticated: state.auth.isAuthenticated
    };
};

export default withRouter(connect(mapStateToProps)(App));
