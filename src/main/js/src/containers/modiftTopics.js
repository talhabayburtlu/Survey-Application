import {Button, Card, CardContent, Divider, Grid, TextField, Typography} from "@material-ui/core";
import Pagination from '@material-ui/lab/Pagination';
import React, {useEffect, useState} from "react";
import {connect} from 'react-redux'
import axios from "axios";


const ModifyTopics = (props) => {
    const [pageCount, setPageCount] = useState(1);
    const [page, setPage] = useState(1)
    const [topics, setTopics] = useState([])
    const [redirect, setRedirect] = useState(false)

    const fetchTopics = () => {
        axios.get("/topics/?page=" + page, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                setPageCount(Math.ceil(res.data.totalDocumentCount / 15.0))
                setTopics(res.data.topics.slice())
            }).catch(e => {

        })
    }

    const updateTopic = (index) => {
        axios.put("/topics/" + topics[index].id, topics[index], {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                fetchTopics()
            }).catch(e => {
        })
    }

    const deleteTopic = (id) => {
        axios.delete("/topics/" + id, {headers: {'Authorization': `Bearer ${props.token}`}})
            .then((res) => {
                fetchTopics()
            }).catch(e => {
        })
    }

    const addNewAnswer = (index) => {
        const newTopics = topics.slice()
        newTopics[index].answers.push({description: ""})
        setTopics(newTopics)
    }

    useEffect(() => {
        if (props.role !== "ADMIN")
            props.history.push("/")
        else
            fetchTopics()
    }, [page]);

    return (
        <React.Fragment>
            <Grid container direction="row" justify="center" alignItems="center">
                <Typography variant="h4" style={{margin: "15px"}}>All Topics To Modify</Typography>
                {topics !== null && topics !== undefined ? topics.map((element, index) => {
                        return (
                            <Grid item xs={12} key={element.id} style={{margin: "15px 100px"}}>
                                <Card>
                                    <CardContent>
                                        <Grid container>
                                            <TextField
                                                id="standard-basic"
                                                label="Description"
                                                value={element.description}
                                                onChange={(e) => {
                                                    const newTopics = topics.slice()
                                                    newTopics[index].description = e.target.value
                                                    setTopics(newTopics)
                                                }}
                                                style={{margin: "15px 0px", width: "100%"}}
                                            >{element.description}</TextField>
                                        </Grid>
                                        <Divider style={{margin: "5px 0px"}}/>
                                        {element.answers.map((answer, answer_index) => {
                                            return (
                                                <Grid container>
                                                    <TextField
                                                        id="standard-basic"
                                                        label="Description"
                                                        value={answer.description}
                                                        onChange={(e) => {
                                                            const newTopics = topics.slice()
                                                            newTopics[index].answers[answer_index].description = e.target.value
                                                            setTopics(newTopics)
                                                        }}
                                                        style={{margin: "15px 0px", width: "85%"}}
                                                    >{element.description}</TextField>
                                                    <Button
                                                        color="secondary"
                                                        variant="contained"
                                                        onClick={() => {
                                                            const newTopics = topics.slice()
                                                            let modifiedAnswers = newTopics[index].answers.slice(0, answer_index);
                                                            modifiedAnswers = modifiedAnswers.concat(newTopics[index].answers.slice(answer_index + 1))
                                                            newTopics[index].answers = modifiedAnswers
                                                            setTopics(newTopics)
                                                        }}
                                                        style={{margin: "15px"}}>Delete answer</Button>
                                                </Grid>
                                            )
                                        })}

                                        <Grid container justify="center">
                                            <Button
                                                color="secondary"
                                                variant="contained"
                                                onClick={() => updateTopic(index)}
                                                style={{margin: "15px"}}>Update Topic</Button>
                                            <Button
                                                color="secondary"
                                                variant="contained"
                                                onClick={() => deleteTopic(element.id)}
                                                style={{margin: "15px"}}>Delete Topic</Button>
                                            <Button
                                                color="secondary"
                                                variant="contained"
                                                onClick={() => addNewAnswer(index)}
                                                style={{margin: "15px"}}>Add new answer</Button>
                                        </Grid>
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

export default connect(mapStateToProps)(ModifyTopics);