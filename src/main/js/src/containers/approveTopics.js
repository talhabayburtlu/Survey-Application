import {Button, Card, CardContent, Divider, Grid, Typography} from "@material-ui/core";
import Pagination from '@material-ui/lab/Pagination';
import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import axios from "axios";


const ApproveTopics = (props) => {
    const [pageCount, setPageCount] = useState(1);
    const [page, setPage] = useState(1)
    const [topics, setTopics] = useState([])
    const [redirect, setRedirect] = useState(false)

    const fetchRequestedTopics = () => {
        axios.get("/topics/requests/?page=" + page, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                setPageCount(Math.ceil(res.data.totalDocumentCount / 15.0))
                setTopics(res.data.topics.slice())
            }).catch(e => {

        })
    }

    const approveTopic = (id) => {
        axios.patch("/topics/requests/", {"approved-topic-id": id}, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                fetchRequestedTopics()
            }).catch(e => {

        })
    }

    const deleteTopic = (id) => {
        axios.delete("/topics/" + id, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                fetchRequestedTopics()
            }).catch(e => {
        })
    }

    useEffect(() => {
        if (props.role !== "ADMIN")
            props.history.push("/")
        else
            fetchRequestedTopics()
    }, []);

    return (
        <React.Fragment>
            <Grid container direction="row" justify="center" alignItems="center">
                <Typography variant="h4" style={{margin: "15px"}}>Available Topics To Submit Answer</Typography>
                {topics !== null && topics !== undefined ? topics.map(element => {
                    return (
                        <Grid item xs={12} key={element.id} style={{margin: "15px 100px"}}>
                            <Card>
                                <CardContent>
                                    <Grid container>
                                        <Typography
                                            variant="h6"
                                            style={{fontWeight: "600", margin: "5px 0px"}}
                                        >{element.description}</Typography>
                                        <Button
                                            variant="contained"
                                            color="secondary"
                                            onClick={() => approveTopic(element.id)}
                                            style={{marginLeft: "auto"}}
                                        >Approve</Button>
                                        <Button
                                            variant="contained"
                                            color="secondary"
                                            onClick={() => deleteTopic(element.id)}
                                            style={{marginLeft: "10px"}}
                                        >Delete</Button>
                                    </Grid>
                                    <Divider style={{margin: "5px 0px"}}/>
                                    {element.answers.map(answer => {
                                        return (
                                            <Typography key={answer.id}
                                                        style={{margin: "5px 0px"}}>{answer.description}</Typography>
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
        role: state.auth.role
    };
};

export default connect(mapStateToProps)(ApproveTopics);