import {AppBar, Button, Toolbar, Typography} from "@material-ui/core";
import React from "react";
import {connect} from 'react-redux'
import * as actions from "../store/actions/index"
import {withRouter} from 'react-router'

const Header = (props) => {
    return (
        <AppBar position="static" style={{visibility: props.isAuthenticated ? "visible" : "hidden"}}>
            <Toolbar>
                <Typography paragraph variant="h5" style={{margin: "0 10px"}}>Survey</Typography>
                <Button
                    color="inherit"
                    onClick={() => {
                        props.history.push("/results")
                    }}
                    style={{margin: "0px 10px"}}
                >Survey Results</Button>
                <Button
                    color="inherit"
                    onClick={() => {
                        props.history.push("/submit")
                    }}
                    style={{margin: "0px 10px"}}
                >Submit Answer</Button>
                <Button
                    color="inherit"
                    onClick={() => {
                        props.history.push("/request")
                    }}
                    style={{margin: "0px 10px"}}
                >{props.role === "ADMIN" ? "Create Topic" : "Request Topic"} </Button>
                {props.role === "ADMIN" ?
                    <Button
                        color="inherit"
                        onClick={() => {
                            props.history.push("/approve")
                        }}
                        style={{margin: "0px 10px", marginLeft: "auto"}}
                    >Approve Topics</Button> : null}
                {props.role === "ADMIN" ?
                    <Button
                        color="inherit"
                        onClick={() => {
                            props.history.push("/modify")
                        }}
                        style={{margin: "0px 10px"}}
                    >Modify Topics</Button> : null}
                <Button
                    color="inherit"
                    onClick={() => {
                        props.onLogout();
                        props.history.push("/login");
                    }}
                    style={{marginLeft: props.role === "ADMIN" ? "0px" : "auto"}}
                >Logout</Button>
            </Toolbar>
        </AppBar>
    )
}

const mapStateToProps = state => {
    return {
        isAuthenticated: state.auth.isAuthenticated,
        role: state.auth.role
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: (token, email, password) => dispatch(actions.logout()),
    };
};

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(Header));