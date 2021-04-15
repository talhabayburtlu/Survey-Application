import {Button, Card, CardContent, Divider, Grid, Typography} from "@material-ui/core";
import Pagination from '@material-ui/lab/Pagination';
import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import * as actions from "../store/actions/index"
import axios from "axios";


const Submit = (props) => {
    const [pageCount, setPageCount] = useState(1);
    const [page, setPage] = useState(1)
    const [topics, setTopics] = useState([])

    const fetchAvailableTopics = () => {
        axios.get("/topics/users?page=" + page, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                setPageCount(Math.ceil(res.data.totalDocumentCount / 15.0))
                setTopics(res.data.topics.slice())
            }).catch(e => {

        })
    }

    const submitAnswer = (id) => {
        axios.post("/topics/users", {"answer-id": id}, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                fetchAvailableTopics()
            }).catch(e => {

        })
    }

    useEffect(() => {
        fetchAvailableTopics()
    }, [page]);

    return (
        <React.Fragment>
            <Grid container direction="row" justify="center" alignItems="center">
                <Typography variant="h4" style={{margin: "15px"}}>Available Topics To Submit Answer</Typography>
                {topics !== null && topics !== undefined ? topics.map(element => {
                    return (
                        <Grid item xs={12} key={element.id} style={{margin: "15px 100px"}}>
                            <Card>
                                <CardContent>
                                    <Typography variant="h6" style={{
                                        fontWeight: "600",
                                        margin: "5px 0px"
                                    }}>{element.description}</Typography>
                                    <Divider style={{margin: "5px 0px"}}/>
                                    {element.answers.map(answer => {
                                        return (
                                            <Button key={answer.id} fullWidth style={{margin: "5px 0px"}}
                                                    onClick={() => submitAnswer(answer.id)}>{answer.description}</Button>
                                        )
                                    })}
                                </CardContent>
                            </Card>
                        </Grid>)
                    }
                ) : null}
                <Pagination page={page} count={pageCount} onChange={(e, v) => setPage(v)} color="primary"
                            style={{textAlign: "center", margin: "25px auto"}}/>
            </Grid>
        </React.Fragment>

    )
}

const mapStateToProps = state => {
    return {
        token: state.auth.token,
        isAuthenticated: state.auth.isAuthenticated,
    };
};

const mapDispatchToProps = dispatch => {
    return {
        onLogout: (token, email, password) => dispatch(actions.logout()),
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Submit);